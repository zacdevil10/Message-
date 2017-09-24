package uk.co.zac_h.message.mmssms;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import uk.co.zac_h.message.mmssms.Message;

public class Transaction {
    private static final String TAG = "Transaction";
    private static final boolean LOCAL_LOGV = true;

    private Context context;

    private boolean saveMessage = true;

    public String SMS_SENT = ".SMS_SENT";
    public String SMS_DELIVERED = ".SMS_DELIVERED";
    public static String NOTIFY_SMS_FAILURE = ".NOTIFY_SMS_FAILURE";
    public static final String NOTIFY_OF_DELIVERY = "";

    public static final long NO_THREAD_ID = 0;

    public Transaction (Context context) {
        this.context = context;

        SMS_SENT = context.getPackageName() + SMS_SENT;
        SMS_DELIVERED = context.getPackageName() + SMS_DELIVERED;

        if (NOTIFY_SMS_FAILURE.equals(".NOTIFY_SMS_FAILURE")) {
            NOTIFY_SMS_FAILURE = context.getPackageName() + NOTIFY_SMS_FAILURE;
        }
    }

    public void sendNewMessage(Message message, long threadId) {
        this.saveMessage = message.getSave();

        //Check if is mms
        if (message.getType() == Message.TYPE_VOICE) {
            //Send voice message
        } else if (message.getType() == Message.TYPE_SMSMMS) {
            if (LOCAL_LOGV) Log.v(TAG, "sending sms");
            sendSmsMessage(message.getText(), message.getAddresses(), threadId, message.getDelay());
        }

    }

    private void sendSmsMessage(String text, String[] addresses, long threadId, int delay) {
        if (LOCAL_LOGV) Log.v(TAG, "message text: " + text);
        Uri messageUri;
        int messageId = 0;
        if (saveMessage) {
            if (LOCAL_LOGV) Log.v(TAG, "saving message");
            for (String address : addresses) {
                Calendar calendar = Calendar.getInstance();
                ContentValues values = new ContentValues();
                values.put("address", address);
                values.put("body", text);
                values.put("date", calendar.getTimeInMillis() + "");
                values.put("read", 1);
                values.put("type", 4);

                if (threadId == NO_THREAD_ID || addresses.length > 1) {
                    //Create thread_id
                    threadId = Utils.getOrCreateThreadId(context, address);
                }

                if (LOCAL_LOGV) Log.v(TAG, "saving message with thread id: " + threadId);

                values.put("thread_id", threadId);
                messageUri = context.getContentResolver().insert(Uri.parse("content://sms/"), values);

                if (LOCAL_LOGV) Log.v(TAG, "inserted to uri: " + messageUri);

                Cursor cursor = null;
                if (messageUri != null) {
                    cursor = context.getContentResolver().query(messageUri, new String[]{"_id"}, null, null, null);
                }
                if (cursor != null && cursor.moveToFirst()) {
                    messageId = cursor.getInt(0);
                }

                if (LOCAL_LOGV) Log.v(TAG, "message id: " + messageId);

                PendingIntent sentPI = PendingIntent.getBroadcast(context, messageId, new Intent(SMS_SENT).putExtra("message_uri", messageUri == null ? "" : messageUri.toString()), PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(context, messageId, new Intent(SMS_DELIVERED).putExtra("message_uri", messageUri == null ? "" : messageUri.toString()), PendingIntent.FLAG_UPDATE_CURRENT);
                ArrayList<PendingIntent> sPI = new ArrayList<>();
                ArrayList<PendingIntent> dPI = new ArrayList<>();

                SmsManager smsManager = SmsManager.getDefault();
                if (LOCAL_LOGV) Log.v(TAG, "found sms manager");

                ArrayList<String> parts = smsManager.divideMessage(text);

                for (int j = 0; j < parts.size(); j++) {
                    sPI.add(saveMessage ? sentPI : null);
                    dPI.add(saveMessage ? deliveredPI : null);
                }

                try {
                    sendDelayedSms(smsManager, address, parts, sPI, dPI, delay, messageUri);
                } catch (Exception e) {
                    try {
                        Toast.makeText(context, "Message could not be sent", Toast.LENGTH_LONG).show();
                    } catch (Exception e1) {
                    }
                }

                if (cursor != null) cursor.close();
            }
        }
    }

    private void sendDelayedSms(final SmsManager smsManager, final String address, final ArrayList<String> parts, final ArrayList<PendingIntent> sPI, final ArrayList<PendingIntent> dPI, final int delay, final Uri messageUri) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (Exception e) {}

                if (messageExistsAfterDelay(messageUri)) {
                    if (LOCAL_LOGV) Log.v(TAG, "message sent after delay");
                    try {
                        smsManager.sendMultipartTextMessage(address, null, parts, sPI, dPI);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private boolean messageExistsAfterDelay(Uri messageUri) {
        Cursor cursor = context.getContentResolver().query(messageUri, new String[] {"_id"}, null, null, null);
        boolean result = cursor != null && cursor.moveToFirst();
        if (cursor != null) cursor.close();
        return result;
    }

    private void sendMmsMessage() {

    }

}

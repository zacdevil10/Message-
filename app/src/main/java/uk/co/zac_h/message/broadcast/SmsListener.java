package uk.co.zac_h.message.broadcast;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import uk.co.zac_h.message.common.LifecycleHandler;
import uk.co.zac_h.message.data.Message;

public class SmsListener extends BroadcastReceiver {

    private Context mContext;

    private String mAddress;
    private String mBody;
    private long mDate;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        System.out.println("Just got a message!");
        if (intent.getExtras() != null) {

            Bundle bundle = intent.getExtras();
            SmsMessage[] smsMessages;
            String format = intent.getStringExtra("format");
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    assert pdus != null;
                    smsMessages = new SmsMessage[pdus.length];
                    for (int i = 0;  i < smsMessages.length; i++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            smsMessages[i] = SmsMessage.createFromPdu((byte[])pdus[i], format);
                        } else {
                            smsMessages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        }

                        SmsMessage smsMessage = smsMessages[i];
                        mBody = smsMessage.getDisplayMessageBody();
                        mAddress = smsMessage.getDisplayOriginatingAddress();
                        mDate = smsMessage.getTimestampMillis();

                        insertMessage();

                        if (LifecycleHandler.isApplicationVisible()) {
                            System.out.println("Application is alive?");
                            Intent updateConvThread = new Intent("sms.received");

                            updateConvThread.putExtra("address", smsMessages[i].getOriginatingAddress());
                            updateConvThread.putExtra("body", smsMessages[i].getMessageBody());
                            updateConvThread.putExtra("timeStamp", String.valueOf(smsMessages[i].getTimestampMillis()));
                            updateConvThread.putExtra("read", "1");
                            updateConvThread.putExtra("messageType", "1");

                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(updateConvThread));
                        }
                    }
                } catch (Exception e) {
                    Log.d("Exception caught", e.getMessage());
                }
            }
        }
    }

    private void insertMessage() {
        ContentValues values = new ContentValues();

        values.put("address", mAddress);
        values.put("body", mBody);
        values.put("date_sent", mDate);

        Uri uri = mContext.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);

        Message message = new Message(mContext, uri);

        //Intent intent = new Intent(mContext, NotificationService.class);


        //message.markSeen();
    }
}

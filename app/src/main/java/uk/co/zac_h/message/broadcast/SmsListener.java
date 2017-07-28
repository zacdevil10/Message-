package uk.co.zac_h.message.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.util.Log;

import uk.co.zac_h.message.common.utils.Contact;
import uk.co.zac_h.message.database.DatabaseHelper;
import uk.co.zac_h.message.database.databaseModel.MessageModel;

public class SmsListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

            Intent updateConvThread = new Intent("sms.received");
            Intent updateLatestView = new Intent("sms.latest");

            DatabaseHelper db = new DatabaseHelper(context);

            Bundle bundle = intent.getExtras();
            SmsMessage[] smsMessages = null;
            String format = intent.getStringExtra("format");
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    assert pdus != null;
                    smsMessages = new SmsMessage[pdus.length];
                    for (int i=0;  i<smsMessages.length; i++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            smsMessages[i] = SmsMessage.createFromPdu((byte[])pdus[i], format);
                        } else {
                            smsMessages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        }

                        String id;

                        if (new Contact(context).getContactIdFromNumber(smsMessages[i].getOriginatingAddress()).equals("")) {
                            id = "-1";
                        } else {
                            id = new Contact(context).getContactIdFromNumber(smsMessages[i].getOriginatingAddress());
                        }

                        MessageModel messageModel = new MessageModel(id, smsMessages[i].getOriginatingAddress(), smsMessages[i].getMessageBody(), String.valueOf(smsMessages[i].getTimestampMillis()), "0", "1");
                        db.addMessages(messageModel);

                        db.close();

                        updateConvThread.putExtra("address", smsMessages[i].getOriginatingAddress());
                        updateConvThread.putExtra("body", smsMessages[i].getMessageBody());
                        updateConvThread.putExtra("timeStamp", String.valueOf(smsMessages[i].getTimestampMillis()));
                        updateConvThread.putExtra("read", "1");
                        updateConvThread.putExtra("messageType", "1");

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(updateConvThread));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(updateLatestView));
                    }
                } catch (Exception e) {
                    Log.d("Exception caught", e.getMessage());
                }
            }
        }
    }
}

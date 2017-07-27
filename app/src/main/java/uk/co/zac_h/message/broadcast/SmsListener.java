package uk.co.zac_h.message.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import uk.co.zac_h.message.database.DatabaseHelper;
import uk.co.zac_h.message.database.databaseModel.MessageModel;

public class SmsListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

            Intent intent1 = new Intent("sms.received");

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

                        MessageModel messageModel = new MessageModel(removeStartOfNumber(smsMessages[i].getOriginatingAddress()), smsMessages[i].getMessageBody(), String.valueOf(smsMessages[i].getTimestampMillis()), "0", "1");
                        db.addMessages(messageModel);

                        intent1.putExtra("address", smsMessages[i].getOriginatingAddress());
                        intent1.putExtra("body", smsMessages[i].getMessageBody());
                        intent1.putExtra("timeStamp", String.valueOf(smsMessages[i].getTimestampMillis()));
                        intent1.putExtra("read", "1");
                        intent1.putExtra("messageType", "1");

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(intent1));
                    }
                } catch (Exception e) {
                    Log.d("Exception caught", e.getMessage());
                }
            }
        }
    }

    private String removeStartOfNumber(String number) {
        String cleanNumber = number;

        if (number.startsWith("+")) {
            cleanNumber = number.substring(3);
            cleanNumber = cleanNumber.replace(" ", "");
        } else if (number.startsWith("0")) {
            cleanNumber = number.substring(1);
            cleanNumber = cleanNumber.replace(" ", "");
        }

        return cleanNumber;
    }

}

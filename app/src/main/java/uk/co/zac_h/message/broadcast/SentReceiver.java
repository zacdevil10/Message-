package uk.co.zac_h.message.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import uk.co.zac_h.message.mmssms.Transaction;

public class SentReceiver extends BroadcastReceiver {
    private static final String TAG = "SentReceiver";
    private static final boolean LOCAL_LOGV = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (LOCAL_LOGV) Log.v(TAG, "marking message as sent");
        Uri uri;

        try {
            uri = Uri.parse(intent.getStringExtra("message_uri"));
            if (uri.equals("")) {
                uri = null;
            }
        } catch (Exception e) {
            uri = null;
        }

        switch (getResultCode()) {
            case Activity.RESULT_OK:
                if (uri != null) {
                    try {
                        if (LOCAL_LOGV) Log.v(TAG, "using supplied uri");
                        ContentValues values = new ContentValues();
                        values.put("type", 2);
                        values.put("read", 1);
                        context.getContentResolver().update(uri, values, null, null);
                    } catch (NullPointerException e) {
                        markFirstAsSent(context);
                    }
                } else {
                    markFirstAsSent(context);
                }
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                if (uri != null) {
                    if (LOCAL_LOGV) Log.v(TAG, "using supplied uri");
                    ContentValues values = new ContentValues();
                    values.put("type", 5);
                    values.put("read", true);
                    values.put("error_code", getResultCode());
                    context.getContentResolver().update(uri, values, null, null);
                } else {
                    if (LOCAL_LOGV) Log.v(TAG, "using first message");
                    Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/outbox"), null, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        String id = cursor.getString(cursor.getColumnIndex("_id"));
                        ContentValues values = new ContentValues();
                        values.put("type", 5);
                        values.put("read", 1);
                        values.put("error_code", getResultCode());
                        context.getContentResolver().update(Uri.parse("content://sms/outbox"), values, "_id=" + id, null);
                        cursor.close();
                    }
                }

                context.sendBroadcast(new Intent(Transaction.NOTIFY_SMS_FAILURE));
        }
    }

    private void markFirstAsSent(Context context) {
        if (LOCAL_LOGV) Log.v(TAG, "using first message");
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/outbox"), null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            ContentValues values = new ContentValues();
            values.put("type", 2);
            values.put("read", 1);
            context.getContentResolver().update(Uri.parse("content://sms/outbox"), values, "_id=" + id, null);
            cursor.close();
        }
    }
}

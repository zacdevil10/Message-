package uk.co.zac_h.message.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import uk.co.zac_h.message.mmssms.Transaction;

public class DeliveredReceiver extends BroadcastReceiver {
    private static final String TAG = "DeliveredReceiver";
    private static final boolean LOCAL_LOGV = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (LOCAL_LOGV) Log.v(TAG, "marking message as delivered");
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
                Intent delivered = new Intent(Transaction.NOTIFY_OF_DELIVERY);
                delivered.putExtra("result", true);
                delivered.putExtra("message_uri", uri == null ? "" : uri.toString());
                context.sendBroadcast(delivered);

                if (uri != null) {
                    ContentValues values = new ContentValues();
                    values.put("status", "0");
                    values.put("date_sent", Calendar.getInstance().getTimeInMillis());
                    values.put("read", true);
                    context.getContentResolver().update(uri, values, null, null);
                } else {
                    Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/sent"), null, null, null, "date desc");

                    // mark message as delivered in database
                    if (cursor != null && cursor.moveToFirst()) {
                        String id = cursor.getString(cursor.getColumnIndex("_id"));
                        ContentValues values = new ContentValues();
                        values.put("status", "0");
                        values.put("date_sent", Calendar.getInstance().getTimeInMillis());
                        values.put("read", true);
                        context.getContentResolver().update(Uri.parse("content://sms/sent"), values, "_id=" + id, null);
                        cursor.close();
                    }
                }
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}

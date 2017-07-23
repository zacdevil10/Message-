package uk.co.zac_h.message.database;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class MessageSync {

    public MessageSync(Context context) {

        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);



    }

}

package uk.co.zac_h.message.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.Date;

import uk.co.zac_h.message.database.databaseModel.MessageModel;

public class MessageSync {

    private DatabaseHelper db;

    public MessageSync(Context context) {
        Uri uri = Uri.parse("content://sms");
        db = new DatabaseHelper(context);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        int indexAddress = cursor.getColumnIndex("address");
        int indexBody = cursor.getColumnIndex("body");
        int indexDate = cursor.getColumnIndex("date");
        int indexRead = cursor.getColumnIndex("read");

        if (indexBody < 0 || !cursor.moveToFirst()) return;

        do {
            String address = cursor.getString(indexAddress);
            String body = cursor.getString(indexBody);
            Integer date = cursor.getInt(indexDate);
            Integer read = cursor.getInt(indexRead);

            MessageModel messageModel = new MessageModel(address, body, date, read);

            System.out.println(messageModel.getBody() + "    HAS BEEN SENT TO: " + messageModel.getNumber());

            db.addMessages(messageModel);
        } while (cursor.moveToNext());

    }
}

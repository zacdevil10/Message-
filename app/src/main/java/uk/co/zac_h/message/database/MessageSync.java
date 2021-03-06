package uk.co.zac_h.message.database;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import uk.co.zac_h.message.common.utils.Contact;
import uk.co.zac_h.message.database.databaseModel.MessageModel;

public class MessageSync extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final ProgressDialog progressDialog;

    public MessageSync(Context context, ProgressDialog progressDialog) {
        this.context = context;
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
        progressDialog.setCancelable(true);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Uri uri = Uri.parse("content://sms");
        DatabaseHelper db = new DatabaseHelper(context);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        int indexAddress = 0;
        int indexBody = 0;
        int indexDate = 0;
        int indexRead = 0;
        int indexPerson = 0;

        if (cursor != null) {
            indexAddress = cursor.getColumnIndex("address");
            indexBody = cursor.getColumnIndex("body");
            indexDate = cursor.getColumnIndex("date");
            indexRead = cursor.getColumnIndex("read");
            indexPerson = cursor.getColumnIndex("type");
        }

        if (indexBody < 0 || cursor != null && !cursor.moveToFirst()) return null;

        String id;

        do {
            assert cursor != null;
            if (new Contact(context).getContactIdFromNumber(cursor.getString(indexAddress)).equals("")) {
                id = "-1";
            } else {
                id = new Contact(context).getContactIdFromNumber(cursor.getString(indexAddress));
            }
            String number = cursor.getString(indexAddress);
            String body = cursor.getString(indexBody);
            String date = cursor.getString(indexDate);
            String read = cursor.getString(indexRead);
            String person = cursor.getString(indexPerson);

            MessageModel messageModel = new MessageModel(id, number, body, date, read, person);

            db.addMessages(messageModel);
        } while (cursor.moveToNext());

        cursor.close();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
    }
}

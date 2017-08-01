package uk.co.zac_h.message.database;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import uk.co.zac_h.message.common.sync.UpdateContactId;
import uk.co.zac_h.message.database.databaseModel.MessageModel;

public class MessageSync extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final ProgressDialog progressDialog;

    private DatabaseHelper db;
    private Cursor cursor;

    public MessageSync(Context context, ProgressDialog progressDialog) {
        this.context = context;
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
        progressDialog.setCancelable(false);

        db = new DatabaseHelper(context);
        Uri uri = Uri.parse("content://sms");
        cursor = context.getContentResolver().query(uri, null, null, null, null);
    }

    @Override
    protected Void doInBackground(Void... params) {
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

        if (cursor != null) {
            progressDialog.setMax(cursor.getCount());
        }

        String id;

        do {
            assert cursor != null;

            id = "-1";
            String number = cursor.getString(indexAddress);
            String body = cursor.getString(indexBody);
            String date = cursor.getString(indexDate);
            String read = cursor.getString(indexRead);
            String person = cursor.getString(indexPerson);

            MessageModel messageModel = new MessageModel(id, number, body, date, read, person);

            db.addMessages(messageModel);
            progressDialog.setProgress(cursor.getPosition());
        } while (cursor.moveToNext());


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
        cursor.close();
        db.close();

        new UpdateContactId(context).execute();
    }
}

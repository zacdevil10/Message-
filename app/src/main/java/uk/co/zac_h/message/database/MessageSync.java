package uk.co.zac_h.message.database;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import uk.co.zac_h.message.database.databaseModel.MessageModel;

public class MessageSync extends AsyncTask<Void, Void, Void> {

    private DatabaseHelper db;

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
        db = new DatabaseHelper(context);
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

        do {
            assert cursor != null;
            String address = removeStartOfNumber(cursor.getString(indexAddress));
            String body = cursor.getString(indexBody);
            String date = cursor.getString(indexDate);
            String read = cursor.getString(indexRead);
            String person = cursor.getString(indexPerson);

            MessageModel messageModel = new MessageModel(address, body, date, read, person);

            db.addMessages(messageModel);
        } while (cursor.moveToNext());

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
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

package uk.co.zac_h.message.common.sync;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uk.co.zac_h.message.common.utils.Contact;
import uk.co.zac_h.message.common.utils.Time;
import uk.co.zac_h.message.conversations.conversationsadapter.ConversationsAdapter;
import uk.co.zac_h.message.database.DatabaseHelper;
import uk.co.zac_h.message.database.databaseModel.MessageModel;

public class SyncLatestMessages extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final RecyclerView latestMessagesList;

    private final ArrayList<String> thread_id = new ArrayList<>();
    private final ArrayList<String> id = new ArrayList<>();
    private final ArrayList<String> number = new ArrayList<>();
    private final ArrayList<String> name = new ArrayList<>();
    private final ArrayList<String> body = new ArrayList<>();
    private final ArrayList<String> read = new ArrayList<>();
    private final ArrayList<String> type = new ArrayList<>();
    private final ArrayList<String> timeStamp = new ArrayList<>();

    private Cursor cursor;

    public SyncLatestMessages(Context context, RecyclerView latestMessagesList) {
        this.context = context;
        this.latestMessagesList = latestMessagesList;
    }

    @Override
    protected void onPreExecute() {
        Uri uri = Uri.parse("content://mms-sms/conversations/");
        cursor = context.getContentResolver().query(uri, null, null, null, "date DESC");
    }

    @Override
    protected Void doInBackground(Void... params) {
        int indexId = 0;
        int indexAddress = 0;
        int indexBody = 0;
        int indexDate = 0;
        int indexRead = 0;
        int indexPerson = 0;

        if (cursor != null) {
            indexId = cursor.getColumnIndex("thread_id");
            indexAddress = cursor.getColumnIndex("address");
            indexBody = cursor.getColumnIndex("body");
            indexDate = cursor.getColumnIndex("date");
            indexRead = cursor.getColumnIndex("read");
            indexPerson = cursor.getColumnIndex("type");
        }

        if (indexAddress < 0 || cursor != null && !cursor.moveToFirst()) return null;

        do {
            assert cursor != null;
            String idFinder = new Contact(context).getContactIdFromNumber(cursor.getString(indexAddress));
            thread_id.add(cursor.getString(indexId));
            id.add(idFinder);
            number.add(cursor.getString(indexAddress));
            if (idFinder.equals("-1")) {
                name.add(cursor.getString(indexAddress));
            } else {
                name.add(new Contact(context).getContactNameFromID(idFinder));
            }
            body.add(cursor.getString(indexBody));
            read.add(cursor.getString(indexRead));
            type.add(cursor.getString(indexPerson));
            timeStamp.add(new Time().convertMessageDate(Long.valueOf(cursor.getString(indexDate))));

            System.out.println(cursor.getString(cursor.getColumnIndex("_id")));

        } while (cursor.moveToNext());

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        cursor.close();
        ConversationsAdapter conversationsAdapter = new ConversationsAdapter(context, thread_id, id, number, name, body, read, type, timeStamp);
        latestMessagesList.setAdapter(conversationsAdapter);
    }
}

package uk.co.zac_h.message.common.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.zac_h.message.common.utils.Contact;
import uk.co.zac_h.message.common.utils.Time;
import uk.co.zac_h.message.conversations.conversationsadapter.ConversationsAdapter;
import uk.co.zac_h.message.database.DatabaseHelper;
import uk.co.zac_h.message.database.databaseModel.MessageModel;

public class SyncLatestMessages extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final RecyclerView latestMessagesList;

    private final ArrayList<String> id = new ArrayList<>();
    private final ArrayList<String> number = new ArrayList<>();
    private final ArrayList<String> name = new ArrayList<>();
    private final ArrayList<String> body = new ArrayList<>();
    private final ArrayList<String> read = new ArrayList<>();
    private final ArrayList<String> type = new ArrayList<>();
    private final ArrayList<String> timeStamp = new ArrayList<>();

    public SyncLatestMessages(Context context, RecyclerView latestMessagesList) {
        this.context = context;
        this.latestMessagesList = latestMessagesList;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DatabaseHelper db = new DatabaseHelper(context);
        List<MessageModel> messageModels = db.getLatestMessages();
        for (MessageModel messageModel: messageModels) {
            id.add(messageModel.getId());
            number.add(messageModel.getNumber());
            if (messageModel.getId().equals("-1")) {
                name.add(messageModel.getNumber());
            } else {
                name.add(new Contact(context).getContactNameFromID(messageModel.getId()));
            }
            body.add(messageModel.getBody());
            read.add(messageModel.getRead());
            type.add(messageModel.getMessageType());
            timeStamp.add(new Time().convertMessageDate(Long.valueOf(messageModel.getDate())));
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        ConversationsAdapter conversationsAdapter = new ConversationsAdapter(context, id, number, name, body, read, type, timeStamp);
        latestMessagesList.setAdapter(conversationsAdapter);
    }
}

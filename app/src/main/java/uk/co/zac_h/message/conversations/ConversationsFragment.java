package uk.co.zac_h.message.conversations;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uk.co.zac_h.message.R;
import uk.co.zac_h.message.conversations.conversationsadapter.ConversationsAdapter;
import uk.co.zac_h.message.database.DatabaseHelper;
import uk.co.zac_h.message.database.databaseModel.MessageModel;
import uk.co.zac_h.message.database.databaseModel.ProfileModel;

public class ConversationsFragment extends Fragment {

    private DatabaseHelper db;

    private final ArrayList<String> number = new ArrayList<>();
    private final ArrayList<String> body = new ArrayList<>();
    private final ArrayList<String> type = new ArrayList<>();
    private final ArrayList<String> timeStamp = new ArrayList<>();

    public ConversationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        db = new DatabaseHelper(getActivity());
        List<MessageModel> messageModels = db.getLatestMessages();
        for (MessageModel messageModel: messageModels) {
            number.add(getContactName(getContext(), messageModel.getNumber()));
            body.add(messageModel.getBody());
            type.add(messageModel.getMessageType());
            timeStamp.add(messageModel.getDate());
            System.out.println(messageModel.getNumber() + ", Body: " + messageModel.getBody() + ", Date:" + messageModel.getDate() + ", Read? " + messageModel.getRead() + ", Message Type: " + messageModel.getMessageType());
        }

        final RecyclerView latestMessagesList = (RecyclerView) view.findViewById(R.id.latestMessagesList);
        latestMessagesList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        latestMessagesList.setLayoutManager(layoutManager);
        final ConversationsAdapter conversationsAdapter = new ConversationsAdapter(getActivity(), number, body, type, timeStamp);

        latestMessagesList.setAdapter(conversationsAdapter);

        return view;
    }

    public void requestMessages() {

    }

    private String name;

    public String getContactName(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        number = "0" + number;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        if (cursor == null) {
            return null;
        }

        name = number;

        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            //name = number;
        }

        cursor.close();

        return name;
    }

}

package uk.co.zac_h.message.conversations;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.zac_h.message.R;
import uk.co.zac_h.message.conversations.conversationsadapter.ConversationsAdapter;
import uk.co.zac_h.message.database.DatabaseHelper;
import uk.co.zac_h.message.database.databaseModel.MessageModel;

public class ConversationsFragment extends Fragment {

    private final ArrayList<String> number = new ArrayList<>();
    private final ArrayList<String> nameFinal = new ArrayList<>();
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

        DatabaseHelper db = new DatabaseHelper(getActivity());
        List<MessageModel> messageModels = db.getLatestMessages();
        for (MessageModel messageModel: messageModels) {
            number.add(messageModel.getNumber());
            nameFinal.add(getContactName(getContext(), messageModel.getNumber()));
            body.add(messageModel.getBody());
            type.add(messageModel.getMessageType());
            timeStamp.add(convertMessageDate(Long.valueOf(messageModel.getDate())));
        }

        final RecyclerView latestMessagesList = (RecyclerView) view.findViewById(R.id.latestMessagesList);
        latestMessagesList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        latestMessagesList.setLayoutManager(layoutManager);
        final ConversationsAdapter conversationsAdapter = new ConversationsAdapter(getActivity(), number, nameFinal, body, type, timeStamp);

        latestMessagesList.setAdapter(conversationsAdapter);

        return view;
    }

    public String getContactName(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        if (!number.contains("[a-zA-Z]+") && number.length() == 10) {
            number = "0" + number;
        }
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        if (cursor == null) {
            return null;
        }

        String name = number;

        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        cursor.close();

        return name;
    }

    String newDate;
    Date date;

    public String convertMessageDate(Long dateMilli) {
        long currentTime = System.currentTimeMillis();

        date = new Date(dateMilli);
        Date currentDate = new Date(currentTime);

        SimpleDateFormat lessThanSevenDays = new SimpleDateFormat("EEE", Locale.UK);
        SimpleDateFormat moreThanSevenDays = new SimpleDateFormat("MMM dd", Locale.UK);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 7);

        if (calendar.getTime().compareTo(currentDate) < 0) {
            newDate = moreThanSevenDays.format(date);
        } else {
            newDate = lessThanSevenDays.format(date);
        }

        return newDate;
    }

}

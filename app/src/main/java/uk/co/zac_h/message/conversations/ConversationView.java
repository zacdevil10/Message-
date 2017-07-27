package uk.co.zac_h.message.conversations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.zac_h.message.R;
import uk.co.zac_h.message.conversations.conversationsadapter.ConversationsViewAdapter;
import uk.co.zac_h.message.database.DatabaseHelper;
import uk.co.zac_h.message.database.databaseModel.MessageModel;

public class ConversationView extends AppCompatActivity {

    String name;
    String number;

    DatabaseHelper db;

    BroadcastReceiver getNewSMS;

    private final ArrayList<String> body = new ArrayList<>();
    private final ArrayList<String> read = new ArrayList<>();
    private final ArrayList<String> messageType = new ArrayList<>();
    private final ArrayList<String> timeStamp = new ArrayList<>();

    private static final int NUMBER_OF_COLORS = 8;
    private TypedArray colors;

    private SmsManager smsManager;

    private ConversationsViewAdapter conversationsViewAdapter;
    private RecyclerView conversationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_conversation_view);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            number = bundle.getString("number");
        }

        final Resources resources = getResources();
        colors = resources.obtainTypedArray(R.array.letter_tile_colors);
        int color = pickColor(name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        toolbar.setBackgroundColor(color);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        smsManager = SmsManager.getDefault();

        db = new DatabaseHelper(this);
        List<MessageModel> messageModels = db.getMessagesForNumber(number);
        for (MessageModel messageModel: messageModels) {
            body.add(messageModel.getBody());
            read.add(messageModel.getRead());
            messageType.add(messageModel.getMessageType());
            timeStamp.add(convertMessageDate(Long.valueOf(messageModel.getDate())));
        }

        conversationsList = (RecyclerView) findViewById(R.id.conversationRecycler);
        conversationsList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        conversationsList.setLayoutManager(layoutManager);
        conversationsViewAdapter = new ConversationsViewAdapter(this, body, timeStamp, read, messageType, color);

        conversationsList.setAdapter(conversationsViewAdapter);

        getNewSMS = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String bodyString = intent.getStringExtra("body");
                String timeStampString = intent.getStringExtra("timeStamp");
                String readString = intent.getStringExtra("read");
                String typeString = intent.getStringExtra("messageType");

                body.add(bodyString);
                read.add(readString);
                messageType.add(typeString);
                timeStamp.add(timeStampString);

                conversationsViewAdapter.notifyDataSetChanged();
                conversationsList.scrollToPosition(body.size() - 1);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(getNewSMS, new IntentFilter("sms.received"));

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String bodyString = ((EditText) findViewById(R.id.editText)).getText().toString();
                String timeString = String.valueOf(System.currentTimeMillis());

                System.out.println(convertMessageDate(System.currentTimeMillis()));

                MessageModel messageModel = new MessageModel("7803293099", bodyString, timeString, "1", "2");
                db.addMessages(messageModel);

                smsManager.sendTextMessage("0" + number, null, bodyString, null, null);

                body.add(bodyString);
                read.add("1");
                messageType.add("2");
                timeStamp.add(timeString);

                conversationsViewAdapter.notifyDataSetChanged();
                conversationsList.scrollToPosition(body.size() - 1);

                ((EditText) findViewById(R.id.editText)).setText("");
            }
        });
    }

    private int pickColor(String key) {
        final int color = Math.abs(key.hashCode()) % NUMBER_OF_COLORS;
        try {
            return colors.getColor(color, Color.BLACK);
        } finally {
            colors.recycle();
        }
    }

    String newDate;
    Date date;

    //TODO: Put this in a class
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

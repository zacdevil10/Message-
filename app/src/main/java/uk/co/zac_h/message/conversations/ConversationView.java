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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.zac_h.message.MainActivity;
import uk.co.zac_h.message.R;
import uk.co.zac_h.message.common.utils.Contact;
import uk.co.zac_h.message.common.utils.Time;
import uk.co.zac_h.message.conversations.conversationsadapter.ConversationsViewAdapter;
import uk.co.zac_h.message.database.DatabaseHelper;
import uk.co.zac_h.message.database.databaseModel.MessageModel;

public class ConversationView extends AppCompatActivity {

    String name;
    String number;
    String id;

    DatabaseHelper db;

    BroadcastReceiver getNewSMS;

    private final ArrayList<String> body = new ArrayList<>();
    private final ArrayList<String> read = new ArrayList<>();
    private final ArrayList<String> messageType = new ArrayList<>();
    private final ArrayList<String> timeStamp = new ArrayList<>();
    private final ArrayList<Boolean> animation = new ArrayList<>();

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
            id = bundle.getString("id");
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
        List<MessageModel> messageModels = db.getMessagesForNumber(number, id);
        for (MessageModel messageModel: messageModels) {
            body.add(messageModel.getBody());
            read.add(messageModel.getRead());
            messageType.add(messageModel.getMessageType());
            timeStamp.add(new Time().convertMessageDate(Long.valueOf(messageModel.getDate())));
            animation.add(false);
        }
        db.close();

        conversationsList = (RecyclerView) findViewById(R.id.conversationRecycler);
        conversationsList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        conversationsList.setLayoutManager(layoutManager);
        conversationsViewAdapter = new ConversationsViewAdapter(this, body, timeStamp, read, messageType, color, animation);

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
                animation.add(true);

                conversationsViewAdapter.notifyDataSetChanged();
                conversationsList.scrollToPosition(body.size() - 1);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(getNewSMS, new IntentFilter("sms.received"));

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String bodyString = ((EditText) findViewById(R.id.editText)).getText().toString();
                String timeString = String.valueOf(System.currentTimeMillis() - 3000);

                if (!bodyString.equals("")) {
                    Intent updateLatestView = new Intent("sms.latest");
                    //TODO: CHANGE THIS!
                    String id;

                    if (new Contact(ConversationView.this).getContactIdFromNumber(number).equals("")) {
                        id = "-1";
                    } else {
                        id = new Contact(ConversationView.this).getContactIdFromNumber(number);
                    }

                    MessageModel messageModel = new MessageModel(id, number, bodyString, timeString, "1", "2");
                    db.addMessages(messageModel);

                    smsManager.sendTextMessage(number, null, bodyString, null, null);

                    body.add(bodyString);
                    read.add("1");
                    messageType.add("2");
                    timeStamp.add(timeString);
                    animation.add(true);

                    conversationsViewAdapter.notifyDataSetChanged();
                    conversationsList.scrollToPosition(body.size() - 1);

                    ((EditText) findViewById(R.id.editText)).setText("");

                    LocalBroadcastManager.getInstance(ConversationView.this).sendBroadcast(new Intent(updateLatestView));
                }
            }
        });

        ((EditText) findViewById(R.id.editText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    ((ImageView) findViewById(R.id.send)).setImageResource(R.drawable.ic_send_purple_24dp);
                } else {
                    ((ImageView) findViewById(R.id.send)).setImageResource(R.drawable.ic_send_black_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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

}

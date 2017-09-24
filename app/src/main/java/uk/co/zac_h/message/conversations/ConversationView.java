package uk.co.zac_h.message.conversations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import uk.co.zac_h.message.R;
import uk.co.zac_h.message.common.CustomSmsManager;
import uk.co.zac_h.message.common.utils.Time;
import uk.co.zac_h.message.conversations.conversationsadapter.ConversationsViewAdapter;
import uk.co.zac_h.message.database.DatabaseHelper;
import uk.co.zac_h.message.mmssms.Message;
import uk.co.zac_h.message.mmssms.Transaction;

public class ConversationView extends AppCompatActivity {

    private String name;
    private String number;
    private String thread_id;

    private DatabaseHelper db;

    private final ArrayList<String> body = new ArrayList<>();
    private final ArrayList<String> read = new ArrayList<>();
    private final ArrayList<String> messageType = new ArrayList<>();
    private final ArrayList<String> timeStamp = new ArrayList<>();
    private final ArrayList<Boolean> animation = new ArrayList<>();

    private static final int NUMBER_OF_COLORS = 8;
    private TypedArray colors;
    //int color;

    private SmsManager smsManager;

    private ConversationsViewAdapter conversationsViewAdapter;
    private RecyclerView conversationsList;

    ConstraintLayout sendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_conversation_view);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            thread_id = bundle.getString("thread_id");
            number = bundle.getString("number");
        }

        final Resources resources = getResources();
        colors = resources.obtainTypedArray(R.array.letter_tile_colors);
        //int color = pickColor(name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        smsManager = SmsManager.getDefault();

        conversationsList = (RecyclerView) findViewById(R.id.conversationRecycler);
        conversationsList.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        conversationsList.setLayoutManager(layoutManager);

        sendMessage = (ConstraintLayout) findViewById(R.id.sendMessage);

        new GetFullConv().execute();

        BroadcastReceiver getNewSMS = new BroadcastReceiver() {
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

                new CustomSmsManager(ConversationView.this, Long.valueOf(thread_id)).markAsRead();

                conversationsViewAdapter.notifyDataSetChanged();
                conversationsList.scrollToPosition(body.size() - 1);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(getNewSMS, new IntentFilter("sms.received"));

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String bodyString = ((EditText) findViewById(R.id.editText)).getText().toString();
                final String timeString = String.valueOf(System.currentTimeMillis() - 3000);

                if (!bodyString.equals("")) {

                    Message message = new Message(bodyString, number);
                    new Transaction(ConversationView.this).sendNewMessage(message, Long.valueOf(thread_id));

                    body.add(bodyString);
                    read.add("1");
                    messageType.add("2");
                    timeStamp.add(timeString);
                    animation.add(true);

                    conversationsViewAdapter.notifyDataSetChanged();
                    conversationsList.scrollToPosition(body.size() - 1);

                    ((EditText) findViewById(R.id.editText)).setText("");
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
                    conversationsList.setPadding(0, 0, 0, sendMessage.getHeight());
                } else {
                    ((ImageView) findViewById(R.id.send)).setImageResource(R.drawable.ic_send_black_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final Animation animationUp = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_bottom);
        final Animation animationDown = AnimationUtils.loadAnimation(this, R.anim.anim_slide_down_bottom);

        conversationsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visible = layoutManager.getChildCount();
                int total = layoutManager.getItemCount();
                int past = layoutManager.findFirstVisibleItemPosition();

                if (past + visible >= total  && sendMessage.getVisibility() == View.INVISIBLE) {
                    sendMessage.startAnimation(animationUp);
                    sendMessage.setVisibility(View.VISIBLE);
                }

                if (dy < -150 && (!findViewById(R.id.editText).hasFocus() || ((EditText) findViewById(R.id.editText)).length() == 0)&& sendMessage.getVisibility() == View.VISIBLE) {
                    sendMessage.startAnimation(animationDown);
                    sendMessage.setVisibility(View.INVISIBLE);
                } else if (dy > 30 && sendMessage.getVisibility() == View.INVISIBLE){
                    sendMessage.startAnimation(animationUp);
                    sendMessage.setVisibility(View.VISIBLE);
                }

            }
        });

        findViewById(R.id.showEditText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendMessage.getVisibility() == View.INVISIBLE){
                    sendMessage.startAnimation(animationUp);
                    sendMessage.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private int pickColor(String key) {
        final int color = Math.abs(key.hashCode()) % NUMBER_OF_COLORS;
        try {
            return colors.getColor(color, Color.BLACK);
        } finally {
            colors.recycle();
        }
    }

    private class GetFullConv extends AsyncTask<Void, Void, Void> {

        private Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Uri uri = Uri.parse("content://sms/");
            cursor = getContentResolver().query(uri, null, "thread_id=" + thread_id, null, "date ASC");
        }

        @Override
        protected Void doInBackground(Void... params) {
            int indexBody = 0;
            int indexDate = 0;
            int indexRead = 0;
            int indexPerson = 0;

            if (cursor != null) {
                indexBody = cursor.getColumnIndex("body");
                indexDate = cursor.getColumnIndex("date");
                indexRead = cursor.getColumnIndex("read");
                indexPerson = cursor.getColumnIndex("type");
            }

            if (indexBody < 0 || cursor != null && !cursor.moveToFirst()) return null;

            do {
                assert cursor != null;
                body.add(cursor.getString(indexBody));
                read.add(cursor.getString(indexRead));
                messageType.add(cursor.getString(indexPerson));
                timeStamp.add(new Time().convertMessageDate(Long.valueOf(cursor.getString(indexDate))));
                animation.add(false);
            } while (cursor.moveToNext());
            /*
            db = new DatabaseHelper(ConversationView.this);
            List<MessageModel> messageModels = db.getMessagesForNumber(number, id);
            for (MessageModel messageModel: messageModels) {
                body.add(messageModel.getBody());
                read.add(messageModel.getRead());
                messageType.add(messageModel.getMessageType());
                timeStamp.add(new Time().convertMessageDate(Long.valueOf(messageModel.getDate())));
                animation.add(false);
            }
            //*/

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            conversationsViewAdapter = new ConversationsViewAdapter(ConversationView.this, body, timeStamp, read, messageType, pickColor(name), animation);
            conversationsList.setAdapter(conversationsViewAdapter);

            cursor.close();
        }
    }

}

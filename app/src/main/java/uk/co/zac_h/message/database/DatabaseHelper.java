package uk.co.zac_h.message.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import uk.co.zac_h.message.database.databaseModel.MessageModel;
import uk.co.zac_h.message.database.databaseModel.ProfileModel;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "cache.db";
    private static final Integer DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS SMS_MESSAGES(number TEXT, body TEXT, date TEXT, read TEXT, message_type TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS PROFILE(name TEXT DEFAULT 'Unknown', firstRun INTEGER DEFAULT 0)");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS SMS_MESSAGES");
        db.execSQL("DROP TABLE IF EXISTS PROFILE");
        onCreate(db);
    }

    //SMS_MESSAGES

    //Add messages to SMS_MESSAGES table
    public void addMessages(MessageModel messageModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("number", messageModel.getNumber());
        values.put("body", messageModel.getBody());
        values.put("date", messageModel.getDate());
        values.put("read", messageModel.getRead());
        values.put("message_type", messageModel.getMessageType());

        db.insert("SMS_MESSAGES", null, values);
    }

    //Add profile to PROFILE table
    public void addProfile(ProfileModel profileModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", profileModel.getName());
        values.put("firstRun", profileModel.getFirstRun());

        db.insert("PROFILE", null, values);
    }

    //Get messages from SMS_MESSAGES table
    public List<MessageModel> getAllMessages() {
        List<MessageModel> messageModelList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM SMS_MESSAGES", null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel messageModel = new MessageModel();
                messageModel.setNumber(cursor.getString(cursor.getColumnIndex("number")));
                messageModel.setBody(cursor.getString(cursor.getColumnIndex("body")));
                messageModel.setDate(cursor.getString(cursor.getColumnIndex("date")));
                messageModel.setRead(cursor.getString(cursor.getColumnIndex("read")));
                messageModel.setMessageType(cursor.getString(cursor.getColumnIndex("message_type")));
                messageModelList.add(messageModel);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return messageModelList;
    }

    //Get latest message for each number
    public List<MessageModel> getLatestMessages() {
        List<MessageModel> messageModelList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM SMS_MESSAGES t1 WHERE t1.date = (SELECT MAX(t2.date) FROM SMS_MESSAGES t2 WHERE t2.number=t1.number)", null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel messageModel = new MessageModel();
                messageModel.setNumber(cursor.getString(cursor.getColumnIndex("number")));
                messageModel.setBody(cursor.getString(cursor.getColumnIndex("body")));
                messageModel.setDate(cursor.getString(cursor.getColumnIndex("date")));
                messageModel.setRead(cursor.getString(cursor.getColumnIndex("read")));
                messageModel.setMessageType(cursor.getString(cursor.getColumnIndex("message_type")));
                messageModelList.add(messageModel);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return messageModelList;
    }

    //Get all messages for a selected number
    public List<MessageModel> getMessagesForNumber(String number) {
        List<MessageModel> messageModelList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM SMS_MESSAGES WHERE number='" + number + "'" + "ORDER BY date ASC", null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel messageModel = new MessageModel();
                messageModel.setBody(cursor.getString(cursor.getColumnIndex("body")));
                messageModel.setDate(cursor.getString(cursor.getColumnIndex("date")));
                messageModel.setRead(cursor.getString(cursor.getColumnIndex("read")));
                messageModel.setMessageType(cursor.getString(cursor.getColumnIndex("message_type")));
                messageModelList.add(messageModel);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return messageModelList;
    }

    //Mark all messages as read for a number
    public void setRead(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SMS_MESSAGES SET read='0' WHERE number='" + number + "'");
    }

    //Get first run
    Integer getFirstRun() {
        Integer firstRun;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT firstRun FROM PROFILE", null);
        cursor.moveToFirst();

        ProfileModel profileModel = new ProfileModel();
        profileModel.setFirstRun(cursor.getInt(cursor.getColumnIndex("firstRun")));

        firstRun = profileModel.getFirstRun();

        cursor.close();

        return firstRun;
    }

    //Set firstRun as 1
    void setFirstRun() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE PROFILE SET firstRun='1'");
    }

    //Get count of profile table
    Integer isProfileEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM PROFILE", null);

        if (cursor != null) {
            cursor.moveToFirst();

            int count = cursor.getInt(0);

            if (count > 0) {
                return 1;
            }

            cursor.close();
        }

        return 0;
    }
}

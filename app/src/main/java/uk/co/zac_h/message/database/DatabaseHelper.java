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
            db.execSQL("CREATE TABLE IF NOT EXISTS SMS_MESSAGES(id TEXT, number TEXT, body TEXT, date TEXT, read TEXT, message_type TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS LATEST_MESSAGE_CACHE(id TEXT, number TEXT, body TEXT, date TEXT, read TEXT, message_type TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS PROFILE(name TEXT DEFAULT 'Unknown', firstRun INTEGER DEFAULT 0)");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS SMS_MESSAGES");
        db.execSQL("DROP TABLE IF EXISTS LATEST_MESSAGE_CACHE");
        db.execSQL("DROP TABLE IF EXISTS PROFILE");
        onCreate(db);
    }

    //SMS_MESSAGES

    //Add messages to SMS_MESSAGES table
    public void addMessages(MessageModel messageModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", messageModel.getId());
        values.put("number", messageModel.getNumber());
        values.put("body", messageModel.getBody());
        values.put("date", messageModel.getDate());
        values.put("read", messageModel.getRead());
        values.put("message_type", messageModel.getMessageType());

        db.insert("SMS_MESSAGES", null, values);
        db.close();
    }

    //Update all users values for ID
    public void updateID(String id, String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SMS_MESSAGES SET id='" + id + "' WHERE number='" + number + "'");
        db.close();
    }

    //If two numbers are the same, use a preferred number
    public void updateNumber(String numberNew, String numberOld) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SMS_MESSAGES SET number='" + numberNew + "' WHERE number='" + numberOld + "'");
        db.close();
    }

    //Add profile to PROFILE table
    public void addProfile(ProfileModel profileModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", profileModel.getName());
        values.put("firstRun", profileModel.getFirstRun());

        db.insert("PROFILE", null, values);
        db.close();
    }

    //Get all numbers
    public List<MessageModel> getAllNumbers() {
        List<MessageModel> messageModelList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT number FROM SMS_MESSAGES t1 WHERE t1.date = (SELECT MAX(t2.date) FROM SMS_MESSAGES t2 WHERE t2.id=t1.id) OR id='-1' AND t1.date = (SELECT MAX(t2.date) FROM SMS_MESSAGES t2 WHERE t2.number=t1.number) ORDER BY date DESC", null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel messageModel = new MessageModel();
                messageModel.setNumber(cursor.getString(cursor.getColumnIndex("number")));
                messageModelList.add(messageModel);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return messageModelList;
    }

    //Get messages from SMS_MESSAGES table
    List<MessageModel> getAllMessages() {
        List<MessageModel> messageModelList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM SMS_MESSAGES", null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel messageModel = new MessageModel();
                messageModel.setId(cursor.getString(cursor.getColumnIndex("id")));
                messageModel.setBody(cursor.getString(cursor.getColumnIndex("body")));
                messageModel.setDate(cursor.getString(cursor.getColumnIndex("date")));
                messageModel.setRead(cursor.getString(cursor.getColumnIndex("read")));
                messageModel.setMessageType(cursor.getString(cursor.getColumnIndex("message_type")));
                messageModelList.add(messageModel);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return messageModelList;
    }

    //Get latest message for each number
    public List<MessageModel> getLatestMessages() {
        List<MessageModel> messageModelList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM SMS_MESSAGES t1 WHERE t1.date = (SELECT MAX(t2.date) FROM SMS_MESSAGES t2 WHERE t2.id=t1.id) OR id='-1' AND t1.date = (SELECT MAX(t2.date) FROM SMS_MESSAGES t2 WHERE t2.number=t1.number) ORDER BY date DESC", null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel messageModel = new MessageModel();
                messageModel.setId(cursor.getString(cursor.getColumnIndex("id")));
                messageModel.setNumber(cursor.getString(cursor.getColumnIndex("number")));
                messageModel.setBody(cursor.getString(cursor.getColumnIndex("body")));
                messageModel.setDate(cursor.getString(cursor.getColumnIndex("date")));
                messageModel.setRead(cursor.getString(cursor.getColumnIndex("read")));
                messageModel.setMessageType(cursor.getString(cursor.getColumnIndex("message_type")));
                messageModelList.add(messageModel);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return messageModelList;
    }

    //Get all messages for a selected number
    public List<MessageModel> getMessagesForNumber(String number, String id) {
        List<MessageModel> messageModelList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor;

        if (id.equals("-1")) {
            cursor = db.rawQuery("SELECT * FROM SMS_MESSAGES WHERE number='" + number + "'" + "ORDER BY date ASC", null);
        } else {
            cursor = db.rawQuery("SELECT * FROM SMS_MESSAGES WHERE id='" + id + "'" + "ORDER BY date ASC", null);
        }

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
        db.close();

        return messageModelList;
    }

    //Mark all messages as read for a number
    public void setRead(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SMS_MESSAGES SET read='1' WHERE number='" + number + "'");
        db.close();
    }

    //LATEST_MESSAGE_CACHE

    //Add messages to LATEST_MESSAGE_CACHE table
    public void addLatestMessages(MessageModel messageModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", messageModel.getId());
        values.put("number", messageModel.getNumber());
        values.put("body", messageModel.getBody());
        values.put("date", messageModel.getDate());
        values.put("read", messageModel.getRead());
        values.put("message_type", messageModel.getMessageType());

        db.insert("LATEST_MESSAGE_CACHE", null, values);
        db.close();
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
        db.close();

        return firstRun;
    }

    //Set firstRun as 1
     void setFirstRun() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", "Unknown");
        values.put("firstRun", "1");

        db.insert("PROFILE", null, values);
        db.close();
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
        db.close();

        return 0;
    }
}

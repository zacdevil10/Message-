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
            db.execSQL("CREATE TABLE IF NOT EXISTS SMS_MESSAGES(number TEXT, body TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS PROFILE(name TEXT, firstRun INTEGER DEFAULT 0)");
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
    public void addMessage(MessageModel messageModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("number", messageModel.getNumber());
        values.put("body", messageModel.getBody());

        db.insert("SMS_MESSAGE", null, values);
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
            } while (cursor.moveToNext());
        }

        cursor.close();

        return messageModelList;
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
}

package uk.co.zac_h.message.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

import uk.co.zac_h.message.common.SmsHelper;

public class Message {

    public static final Uri SMS_CONTENT_PROVIDER = Uri.parse("content://sms/");
    public static final Uri MMS_SMS_CONTENT_PROVIDER = Uri.parse("content://mms-sms/conversations/");

    private Context context;
    private Uri uri;
    private long id;
    private long threadId;
    private String body;
    private String address;
    private String name;
    private long contactId;
    private Bitmap photoBitmap;
    private boolean read;

    public Message(Context context, long id) {
        this.context = context;
        this.id = id;

        uri = Uri.withAppendedPath(MMS_SMS_CONTENT_PROVIDER, "" + id);
    }

    public Message(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;

        Cursor cursor = context.getContentResolver().query(uri, new String[] {SmsHelper.COLUMN_ID}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            id = cursor.getLong(cursor.getColumnIndexOrThrow(SmsHelper.COLUMN_ID));
            cursor.close();
        }
    }

    public long getId() {
        return id;
    }

    public long getThreadId() {
        if (threadId == 0) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(SMS_CONTENT_PROVIDER, new String[]{"thread_id"}, "_id=" + id, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    threadId = cursor.getLong(cursor.getColumnIndexOrThrow("thread_id"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }
        }

        return threadId;
    }

    public boolean isMms() {
        boolean isMms = false;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MMS_SMS_CONTENT_PROVIDER, new String[]{"ct_t"}, "_id=" + id, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                isMms = "application/vnd.wap.multipart.related".equals(cursor.getString(cursor.getColumnIndex("ct_t")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        return isMms;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public long getContactId() {
        return contactId;
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public void markSeen() {
        ContentValues values = new ContentValues();
        values.put("seen", true);

        context.getContentResolver().update(Uri.parse("content://sms/" + getId()), values, null, null);
    }

    public void markRead() {

    }

    public void delete() {

    }

}

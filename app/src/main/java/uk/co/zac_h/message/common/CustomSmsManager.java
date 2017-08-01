package uk.co.zac_h.message.common;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;

public class CustomSmsManager {

    private final Context context;
    private final long threadId;

    private static ContentValues readContentValues;

    private static final String[] UNREAD_PROTECTION = {Telephony.Threads._ID, Telephony.Threads.READ};
    private static final String UNREAD_SELECTION = "(read=0 OR seen=0)";

    public CustomSmsManager(Context context, long threadId) {
        this.context = context;
        this.threadId = threadId;
    }

    private void setReadContentValues() {
        if (readContentValues == null) {
            readContentValues = new ContentValues(2);
            readContentValues.put("read", 1);
            readContentValues.put("seen", 1);
        }
    }

    public void markAsRead() {

        final Uri threadUri = getUri();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (threadUri != null) {
                    setReadContentValues();

                    boolean markRead = true;

                    Cursor cursor = context.getContentResolver().query(threadUri, UNREAD_PROTECTION, UNREAD_SELECTION, null, null);
                    if (cursor != null) {
                        try {
                            markRead = cursor.getCount() > 0;
                            System.out.println(markRead);
                        } finally {
                            cursor.close();
                        }
                    }

                    if (markRead) {
                        context.getContentResolver().update(threadUri, readContentValues, UNREAD_SELECTION, null);
                        System.out.println("Marking as read");
                    }
                }

                return null;
            }
        }.execute();
    }

    private synchronized Uri getUri() {
        if (threadId <= 0) {
            return null;
        }

        return ContentUris.withAppendedId(Telephony.Threads.CONTENT_URI, threadId);
    }

}

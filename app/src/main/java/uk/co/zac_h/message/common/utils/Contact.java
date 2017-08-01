package uk.co.zac_h.message.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class Contact {

    private final Context context;

    public Contact(Context context) {
        this.context = context;
    }

    public String getContactIdFromNumber(String number) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup._ID}, null, null, null);

        if (cursor == null) {
            return number;
        }

        String id = null;

        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
        } else {
            id = "-1";
        }

        cursor.close();

        return id;
    }

    public String getContactNumberFromID(String id) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, Uri.encode(id));
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor == null) return null;

        if (cursor.moveToFirst()) {
            do {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return id;
    }

    public String getContactNameFromNumber(String number) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        if (cursor == null) return null;

        String name = number;

        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        cursor.close();

        return name;
    }

    public String getContactNameFromID(String id) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID + " = ? ", new String[]{id}, null);

        if (cursor == null) {
            return id;
        }

        String name = id;

        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        return name;
    }

}

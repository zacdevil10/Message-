package uk.co.zac_h.message.common;

import android.net.Uri;

public class SmsHelper {

    public static final Uri SMS_CONTENT_PROVIDER = Uri.parse("content://sms/");
    public static final Uri MMS_CONTENT_PROVIDER = Uri.parse("content://mms/");
    public static final Uri MMS_SMS_CONTENT_PROVIDER = Uri.parse("content://mms-sms/conversations/");
    public static final Uri SENT_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/sent");
    public static final Uri DRAFTS_CONTENT_PROVIDER = Uri.parse("content://sms/draft");
    public static final Uri PENDING_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/outbox");
    public static final Uri RECEIVED_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/inbox");
    public static final Uri CONVERSATIONS_CONTENT_PROVIDER = Uri.parse("content://mms-sms/conversations?simple=true");
    public static final Uri ADDRESSES_CONTENT_PROVIDER = Uri.parse("content://mms-sms/canonical-addresses");

    public static final String MAX_MMS_ATTACHMENT_SIZE_UNLIMITED = "unlimited";
    public static final String MAX_MMS_ATTACHMENT_SIZE_300KB = "300kb";
    public static final String MAX_MMS_ATTACHMENT_SIZE_600KB = "600kb";
    public static final String MAX_MMS_ATTACHMENT_SIZE_1MB = "1mb";

    public static final String sortDateDesc = "date DESC";
    public static final String sortDateAsc = "date ASC";

    public static final byte UNREAD = 0;
    public static final byte READ = 1;

    // Attachment types
    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int VIDEO = 2;
    public static final int AUDIO = 3;
    public static final int SLIDESHOW = 4;

    // Columns for SMS content providers
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_THREAD_ID = "thread_id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_RECIPIENT = "recipient_ids";
    public static final String COLUMN_PERSON = "person";
    public static final String COLUMN_SNIPPET = "snippet";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DATE_NORMALIZED = "normalized_date";
    public static final String COLUMN_DATE_SENT = "date_sent";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_ERROR = "error";
    public static final String COLUMN_READ = "read";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_MMS = "ct_t";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_SUB = "sub";
    public static final String COLUMN_MSG_BOX = "msg_box";
    public static final String COLUMN_SUBJECT = "subject";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_SEEN = "seen";
}

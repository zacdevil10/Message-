package uk.co.zac_h.message.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import uk.co.zac_h.message.mmssms.Message;
import uk.co.zac_h.message.mmssms.Transaction;

public class HeadlessSmsSendService extends IntentService {

    public HeadlessSmsSendService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        System.out.println("HeadlessSmsSendService");
        String action = null;
        if (intent != null) {
            action = intent.getAction();
        }
        if (!TelephonyManager.ACTION_RESPOND_VIA_MESSAGE.equals(action)) return;

        Bundle extras = intent.getExtras();
        if (extras != null) {
            String body = extras.getString(Intent.EXTRA_TEXT);
            Uri intentUri = intent.getData();
            String recipients = getRecipients(intentUri);

            if (!TextUtils.isEmpty(recipients) && !TextUtils.isEmpty(body)) {
                String[] destinations = TextUtils.split(recipients, ";");

                Transaction sendTransaction = new Transaction(this);

                Message message = new Message(body, destinations);
                message.setType(Message.TYPE_SMSMMS);

                sendTransaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
                //TODO: Notification here?
            }
        }
    }

    private String getRecipients(Uri uri) {
        String base = uri.getSchemeSpecificPart();
        int pos = base.indexOf('?');
        return (pos == -1) ? base : base.substring(0, pos);
    }
}

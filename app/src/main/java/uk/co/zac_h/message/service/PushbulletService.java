package uk.co.zac_h.message.service;

import com.pushbullet.android.extension.MessagingExtension;

public class PushbulletService extends MessagingExtension {

    @Override
    protected void onMessageReceived(String conversationIden, String body) {

    }

    @Override
    protected void onConversationDismissed(String conversationIden) {

    }

}

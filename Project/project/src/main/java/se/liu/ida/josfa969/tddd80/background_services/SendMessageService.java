package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * Created by Josef on 09/08/14.
 *
 * A service called when sending a message
 * to another user
 */
public class SendMessageService extends IntentService {

    public SendMessageService() {
        super("SendMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String sender = intent.getStringExtra(Constants.SENDER_KEY);
        String receiver = intent.getStringExtra(Constants.RECEIVER_KEY);
        String messageText = intent.getStringExtra(Constants.MESSAGE_TEXT_KEY);
        JsonMethods.sendMessage(sender, receiver, messageText);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.SEND_MESSAGE_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadCastIntent);
    }
}

package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.MessageRecord;

/**
 * Created by Josef on 12/08/14.
 *
 * A service called to get the message feed on a user's profile
 */
public class GetMessageFeedService extends IntentService {

    public GetMessageFeedService() {
        super("GetMessageFeedService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userName = intent.getStringExtra(Constants.USER_NAME_KEY);
        ArrayList<MessageRecord> messages = JsonMethods.getMessageFeed(userName);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.GET_MESSAGE_FEED_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.MESSAGES_KEY, messages);
        sendBroadcast(broadCastIntent);
    }
}

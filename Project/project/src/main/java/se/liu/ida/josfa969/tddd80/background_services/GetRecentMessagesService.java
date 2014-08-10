package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.MessageRecord;

/**
 * Created by Josef on 09/08/14.
 *
 * A service called in order to get the most
 * recent messages sent between two people
 */
public class GetRecentMessagesService extends IntentService {

    public GetRecentMessagesService() {
        super("GetRecentMessagesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userName = intent.getStringExtra(Constants.USER_NAME_KEY);
        String originalUserName = intent.getStringExtra(Constants.ORIGINAL_USER_KEY);
        ArrayList<MessageRecord> recentMessages = JsonMethods.getRecentMessages(userName, originalUserName);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.GET_RECENT_MESSAGES_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.MESSAGES_KEY, recentMessages);
        sendBroadcast(broadCastIntent);
    }
}

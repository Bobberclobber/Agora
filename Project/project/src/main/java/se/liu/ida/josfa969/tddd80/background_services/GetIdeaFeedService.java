package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.IdeaRecord;

/**
 * Created by Josef on 06/08/14.
 *
 * A service called to get the idea feed on a user's profile
 */
public class GetIdeaFeedService extends IntentService {

    public GetIdeaFeedService() {
        super("GetIdeaFeedService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userName = intent.getStringExtra(Constants.USER_NAME_KEY);
        ArrayList<IdeaRecord> ideas = JsonMethods.getIdeaFeed(userName);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.GET_IDEA_FEED_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.IDEAS_KEY, ideas);
        sendBroadcast(broadCastIntent);
    }
}

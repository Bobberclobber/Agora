package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.IdeaRecord;

/**
 * Created by Josef on 11/08/14.
 *
 * A service called when fetching another user's ideas
 */
public class GetOtherUserIdeasService extends IntentService {
    public GetOtherUserIdeasService() {
        super("GetOtherUserIdeasService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userName = intent.getStringExtra(Constants.USER_NAME_KEY);
        ArrayList<IdeaRecord> ideas = JsonMethods.getOtherUserRecentIdeas(userName);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.GET_OTHER_USER_IDEAS_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.IDEAS_KEY, ideas);
        sendBroadcast(broadCastIntent);
    }
}

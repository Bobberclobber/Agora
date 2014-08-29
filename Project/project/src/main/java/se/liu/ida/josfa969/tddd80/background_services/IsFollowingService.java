package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * Created by Josef on 20/08/2014.
 *
 * A service to check if a user is following another
 */
public class IsFollowingService extends IntentService {

    public IsFollowingService() {
        super("IsFollowingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String originalUserName = intent.getStringExtra(Constants.ORIGINAL_USER_KEY);
        String userName = intent.getStringExtra(Constants.USER_NAME_KEY);
        ArrayList<String> userData = intent.getStringArrayListExtra(Constants.USER_DATA_KEY);
        boolean isFollowing = JsonMethods.userIsFollowing(originalUserName, userName);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.IS_FOLLOWING_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.IS_FOLLOWING_KEY, isFollowing);
        if (userData != null) {
            broadCastIntent.putExtra(Constants.USER_DATA_KEY, userData);
        }
        sendBroadcast(broadCastIntent);
    }
}

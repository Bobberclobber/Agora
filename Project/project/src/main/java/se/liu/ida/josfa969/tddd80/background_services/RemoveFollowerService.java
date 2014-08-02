package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * Created by Josef on 01/08/14.
 *
 * A service called when the user clicks the
 * "Un-Follow" button on another user's profile
 */
public class RemoveFollowerService extends IntentService {
    // Gets keys
    private final String ORIGINAL_USER_KEY = Constants.ORIGINAL_USER_KEY;
    private final String TARGET_USER_KEY = Constants.TARGET_USER_KEY;

    public RemoveFollowerService() {
        super("RemoveFollowerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("----------");
        System.out.println("On Handle Intent");
        System.out.println("----------");

        String originalUser = intent.getStringExtra(ORIGINAL_USER_KEY);
        String targetUser = intent.getStringExtra(TARGET_USER_KEY);
        Log.v("AddFollowerService", "Adding " + originalUser + " as follower to " + targetUser);
        JsonMethods.removeFollower(targetUser, originalUser);

        String toastMsg = "No longer following " + targetUser;

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.ADD_FOLLOWER_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.FOLLOW_TOAST_MSG_KEY, toastMsg);
        sendBroadcast(broadCastIntent);
    }
}

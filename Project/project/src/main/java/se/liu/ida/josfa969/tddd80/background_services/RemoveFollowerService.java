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

    public RemoveFollowerService() {
        super("RemoveFollowerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String originalUser = intent.getStringExtra(Constants.ORIGINAL_USER_KEY);
        String targetUser = intent.getStringExtra(Constants.TARGET_USER_KEY);
        Log.d("AddFollowerService", "Adding " + originalUser + " as follower to " + targetUser);
        JsonMethods.removeFollower(targetUser, originalUser);

        String toastMsg = "No longer following " + targetUser;

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.ADD_FOLLOWER_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.FOLLOW_TOAST_MSG_KEY, toastMsg);
        sendBroadcast(broadCastIntent);
    }
}

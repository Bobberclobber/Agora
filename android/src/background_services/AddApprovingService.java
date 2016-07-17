package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * Created by Josef on 05/08/14.
 *
 * A service called when the user clicks the
 * approve button on an idea
 */
public class AddApprovingService extends IntentService {

    public AddApprovingService() {
        super("AddApprovingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String originalUser = intent.getStringExtra(Constants.ORIGINAL_USER_KEY);
        String ideaId = intent.getStringExtra(Constants.IDEA_ID_KEY);
        Log.d("AddApprovingService", "User: " + originalUser + " is now approving idea id: " + ideaId);
        JsonMethods.addApproving(originalUser, ideaId);

        String toastMsg = "Now approving idea ID: " +ideaId;

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.ADD_APPROVING_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.APPROVING_TOAST_MSG_KEY, toastMsg);
        sendBroadcast(broadCastIntent);
    }
}

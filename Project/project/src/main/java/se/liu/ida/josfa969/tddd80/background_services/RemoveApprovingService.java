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
 * un-approve button on an idea
 */
public class RemoveApprovingService extends IntentService {

    public RemoveApprovingService() {
        super("RemoveApprovingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("----------");
        System.out.println("On Handle Intent");
        System.out.println("Remove Approving");
        System.out.println("----------");

        String originalUser = intent.getStringExtra(Constants.ORIGINAL_USER_KEY);
        String ideaId = intent.getStringExtra(Constants.IDEA_ID_KEY);
        System.out.println("Original User: " + originalUser);
        System.out.println("Idea ID: " + ideaId);
        Log.i("RemoveApprovingService", "User: " + originalUser + " no longer approving idea id: " + ideaId);
        JsonMethods.removeApproving(originalUser, ideaId);

        String toastMsg = "No longer approving idea ID: " +ideaId;

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.REMOVE_APPROVING_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.APPROVING_TOAST_MSG_KEY, toastMsg);
        sendBroadcast(broadCastIntent);
    }
}

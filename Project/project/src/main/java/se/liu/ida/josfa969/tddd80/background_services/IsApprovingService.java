package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * Created by Josef on 10/08/14.
 *
 * A service used to check
 */
public class IsApprovingService extends IntentService {

    public IsApprovingService() {
        super("IsApprovingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userName = intent.getStringExtra(Constants.USER_NAME_KEY);
        String ideaId = intent.getStringExtra(Constants.IDEA_ID_KEY);
        boolean isApproving = JsonMethods.userIsApproving(userName, ideaId);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.IS_APPROVING_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.IS_APPROVING_KEY, isApproving);
        sendBroadcast(broadCastIntent);
    }
}

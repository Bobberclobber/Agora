package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.IdeaRecord;

/**
 * Created by Josef on 05/08/14.
 *
 * A service used to get an array list containing
 * all the ideas the user passed along the
 * intent is approving
 */
public class GetApprovingService extends IntentService {

    public GetApprovingService() {
        super("GetApprovingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userName = intent.getStringExtra(Constants.USER_NAME_KEY);
        String originalUser = intent.getStringExtra(Constants.ORIGINAL_USER_KEY);
        ArrayList<IdeaRecord> approving = JsonMethods.getApproving(userName, originalUser);
        Log.d("GetApprovingService", "Fetched ideas: " + approving);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.GET_APPROVING_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.IDEAS_KEY, approving);
        sendBroadcast(broadCastIntent);
    }
}

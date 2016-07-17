package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * Created by Josef on 10/08/14.
 *
 * A service used to get a user's basic data
 */
public class GetUserDataService extends IntentService {

    public GetUserDataService() {
        super("GeUserDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userName = intent.getStringExtra(Constants.USER_NAME_KEY);
        ArrayList<String> userData = JsonMethods.getUserData(userName);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.GET_USER_DATA_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.USER_DATA_KEY, userData);
        sendBroadcast(broadCastIntent);
    }
}

package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.UserRecord;

/**
 * Created by Josef on 05/08/14.
 *
 * A service used to get an array list containing
 * all the users the user passed along the
 * intent is following
 */
public class GetFollowingService extends IntentService {

    public GetFollowingService() {
        super("GetFollowingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("----------");
        System.out.println("On Handle Intent");
        System.out.println("----------");

        String userName = intent.getStringExtra(Constants.USER_NAME_KEY);
        ArrayList<UserRecord> following = JsonMethods.getFollowing(userName);
        Log.i("GetFollowingService", "Fetched users: " + following);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.GET_FOLLOWING_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.PEOPLE_KEY, following);
        sendBroadcast(broadCastIntent);
    }
}

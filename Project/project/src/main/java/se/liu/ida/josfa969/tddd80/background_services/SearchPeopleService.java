package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.UserRecord;

/**
 * Created by Josef on 02/08/14.
 *
 * A service called when the user searches for people
 */
public class SearchPeopleService extends IntentService {

    public SearchPeopleService() {
        super("SearchPeopleService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("----------");
        System.out.println("On Handle Intent");
        System.out.println("----------");

        String identifierString = intent.getStringExtra(Constants.IDENTIFIER_STRING_KEY);
        ArrayList<UserRecord> people = JsonMethods.searchPeople(identifierString);
        Log.i("SearchPeopleService", "Found users: " + people);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.SEARCH_PEOPLE_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.PEOPLE_KEY, people);
        sendBroadcast(broadCastIntent);
    }
}

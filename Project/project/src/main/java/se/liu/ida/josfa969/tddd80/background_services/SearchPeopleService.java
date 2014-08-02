package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

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

    }
}

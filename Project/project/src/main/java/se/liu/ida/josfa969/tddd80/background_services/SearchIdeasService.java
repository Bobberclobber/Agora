package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.item_records.IdeaRecord;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * Created by Josef on 01/08/14.
 *
 * A service called when the user searches for ideas
 */
public class SearchIdeasService extends IntentService {

    public SearchIdeasService() {
        super("SearchIdeasService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("----------");
        System.out.println("On Handle Intent");
        System.out.println("----------");

        String tagString = intent.getStringExtra(Constants.TAG_STRING_KEY);
        ArrayList<IdeaRecord> ideas = JsonMethods.searchIdeas(tagString);
        Log.i("SearchIdeasService", "Found ideas: " + ideas);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.SEARCH_IDEAS_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.IDEAS_KEY, ideas);
        sendBroadcast(broadCastIntent);
    }
}

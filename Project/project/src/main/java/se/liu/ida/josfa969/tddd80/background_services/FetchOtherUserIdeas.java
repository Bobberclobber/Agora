package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.IdeaRecord;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * Created by Josef on 30/07/14.
 *
 * An intent service used to fetch the most
 * recent ideas posted by another user
 * when visiting their profile page
 */
public class FetchOtherUserIdeas extends IntentService {
    private String identifierKey = Constants.IDENTIFIER_KEY;

    public FetchOtherUserIdeas() {
        super("FetchOtherUserIdeas IntentService");
    }

    public FetchOtherUserIdeas(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent initIntent) {
        System.out.println("----------");
        System.out.println("On Handle Intent");
        System.out.println("----------");
        String identifier = initIntent.getStringExtra(identifierKey);
        ArrayList<IdeaRecord> ideas = JsonMethods.getOtherUserRecentIdeas(identifier);

        // Creates an intent which is to be broadcast
        Intent sendDataIntent = new Intent(Constants.IDEA_RETRIEVAL_ACTION);
        // Adds the data to the intent
        sendDataIntent.putExtra(Constants.BROADCAST_IDEAS_KEY, ideas);
        // Broadcasts the intent
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendDataIntent);
    }
}

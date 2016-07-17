package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * Created by Josef on 06/08/14.
 *
 * A service called when the user posts an idea
 */
public class PostIdeaService extends IntentService {

    public PostIdeaService() {
        super("PostIdeaService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String ideaText = intent.getStringExtra(Constants.IDEA_TEXT_KEY);
        String userName = intent.getStringExtra(Constants.USER_NAME_KEY);
        String tags = intent.getStringExtra(Constants.TAG_STRING_KEY);

        // Gets the JSON response from the given input
        String response = JsonMethods.postIdea(ideaText, userName, tags);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.POST_IDEA_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.RESPONSE_KEY, response);
        sendBroadcast(broadCastIntent);
    }
}

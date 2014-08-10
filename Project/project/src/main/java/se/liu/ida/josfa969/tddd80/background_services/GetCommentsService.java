package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.CommentRecord;

/**
 * Created by Josef on 10/08/14.
 *
 * A service used to fetch the comments of an idea
 */
public class GetCommentsService extends IntentService {

    public GetCommentsService() {
        super("GetCommentsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String ideaId = intent.getStringExtra(Constants.IDEA_ID_KEY);
        ArrayList<CommentRecord> comments = JsonMethods.getComments(ideaId);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.GET_COMMENTS_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.COMMENTS_KEY, comments);
        sendBroadcast(broadCastIntent);
    }
}

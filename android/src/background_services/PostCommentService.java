package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * Created by Josef on 10/08/14.
 *
 * A service used to post a comment
 */
public class PostCommentService extends IntentService {

    public PostCommentService() {
        super("PostCommentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String user = intent.getStringExtra(Constants.USER_NAME_KEY);
        String ideaId = intent.getStringExtra(Constants.IDEA_ID_KEY);
        String commentText = intent.getStringExtra(Constants.COMMENT_TEXT_KEY);
        JsonMethods.postComment(user, ideaId, commentText);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.POST_COMMENT_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadCastIntent);
    }
}

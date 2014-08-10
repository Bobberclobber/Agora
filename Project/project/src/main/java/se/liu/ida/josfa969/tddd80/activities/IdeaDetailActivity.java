package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.GetCommentsService;
import se.liu.ida.josfa969.tddd80.background_services.GetUserDataService;
import se.liu.ida.josfa969.tddd80.background_services.PostCommentService;
import se.liu.ida.josfa969.tddd80.fragments.IdeaDetailFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.CommentRecord;
import se.liu.ida.josfa969.tddd80.list_adapters.CommentItemAdapter;

public class IdeaDetailActivity extends Activity {
    private String originalUser;
    private String poster;
    private String ideaText;
    private String tagsText;
    private String approvalNum;
    private String ideaId;

    // An intent used to get user data
    public Intent getUserDataIntent;

    // An array list used to store user data
    private ArrayList<String> userData;

    // Intent to start the get comments service
    public Intent getCommentsIntent;

    // Broadcast receiver
    private ResponseReceiver receiver;

    // Progress dialog
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_detail);

        // Gets data
        Intent initIntent = getIntent();
        originalUser = initIntent.getStringExtra(Constants.ORIGINAL_USER_KEY);
        poster = initIntent.getStringExtra(Constants.POSTER_KEY);
        ideaText = initIntent.getStringExtra(Constants.IDEA_TEXT_KEY);
        tagsText = initIntent.getStringExtra(Constants.TAG_STRING_KEY);
        approvalNum = initIntent.getStringExtra(Constants.APPROVAL_NUM_KEY);
        ideaId = initIntent.getStringExtra(Constants.IDEA_ID_KEY);

        // Creates an intent used to get basic user data
        getUserDataIntent = new Intent(this, GetUserDataService.class);

        // Filters for the receiver
        IntentFilter getCommentsFilter = new IntentFilter(Constants.GET_COMMENTS_RESP);
        IntentFilter postCommentFilter = new IntentFilter(Constants.POST_COMMENT_RESP);
        IntentFilter getUserDataFilter = new IntentFilter(Constants.GET_USER_DATA_RESP);
        getCommentsFilter.addCategory(Intent.CATEGORY_DEFAULT);
        postCommentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getUserDataFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, getCommentsFilter);
        registerReceiver(receiver, postCommentFilter);
        registerReceiver(receiver, getUserDataFilter);

        // Creates the intent to get comments
        getCommentsIntent = new Intent(this, GetCommentsService.class);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new IdeaDetailFragment()).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Gets views to update
        TextView posterView = (TextView) findViewById(R.id.idea_detail_poster);
        TextView ideaTextView = (TextView) findViewById(R.id.idea_detail_idea_text);
        TextView tagsTextView = (TextView) findViewById(R.id.idea_detail_tags);
        TextView approvalNumView = (TextView) findViewById(R.id.idea_detail_approval_num);
        TextView ideaIdView = (TextView) findViewById(R.id.idea_detail_idea_id);

        // Updates the view's data
        posterView.setText(poster);
        ideaTextView.setText(ideaText);
        tagsTextView.setText(tagsText);
        approvalNumView.setText(approvalNum);
        ideaIdView.setText(ideaId);

        progress.setTitle("Loading");
        progress.setMessage("Fetching comments...");
        progress.show();
        getCommentsIntent.putExtra(Constants.IDEA_ID_KEY, ideaId);
        startService(getCommentsIntent);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    public void onCommentClick(View view) {
        EditText commentInput = (EditText) findViewById(R.id.comment_input);
        String commentText = String.valueOf(commentInput.getText());

        if (commentText.equals("")) {
            String toastMsg = "You have to type something";
            Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
        } else {
            progress.setTitle("Loading");
            progress.setMessage("Posting comment...");
            progress.show();
            Intent postCommentIntent = new Intent(this, PostCommentService.class);
            postCommentIntent.putExtra(Constants.USER_NAME_KEY, originalUser);
            postCommentIntent.putExtra(Constants.IDEA_ID_KEY, ideaId);
            postCommentIntent.putExtra(Constants.COMMENT_TEXT_KEY, commentText);
            startService(postCommentIntent);
        }
    }

    public void onIdeaDetailAvatarClick(View view) {
        if (poster != null) {
            progress.setTitle("Loading");
            progress.setMessage("Fetching user data...");
            progress.show();
            getUserDataIntent.putExtra(Constants.USER_NAME_KEY, poster);
            startService(getUserDataIntent);
        }
    }

    private void addListOnClickListener() {
        final ListView commentList = (ListView) findViewById(R.id.comment_list);
        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CommentRecord o = (CommentRecord) commentList.getItemAtPosition(position);
                if (o != null) {
                    progress.setTitle("Loading");
                    progress.setMessage("Fetching user data...");
                    progress.show();
                    getUserDataIntent.putExtra(Constants.USER_NAME_KEY, o.user);
                    startService(getUserDataIntent);
                }
            }
        });
    }

    public void visitOtherProfile() {
        Intent otherProfileIntent = new Intent(this, OtherProfileActivity.class);

        // Gets basic data of the user whose idea
        // was clicked (excluding the users which
        // the user whose idea was clicked is following)
        String clickedUserName = userData.get(0);
        String clickedEMail = userData.get(1);
        String clickedCountry = userData.get(2);
        String clickedCity = userData.get(3);
        String clickedFollowers = userData.get(4);

        // Attaches the basic data to the intent
        otherProfileIntent.putExtra(Constants.USER_NAME_KEY, clickedUserName);
        otherProfileIntent.putExtra(Constants.E_MAIL_KEY, clickedEMail);
        otherProfileIntent.putExtra(Constants.COUNTRY_KEY, clickedCountry);
        otherProfileIntent.putExtra(Constants.CITY_KEY, clickedCity);
        otherProfileIntent.putExtra(Constants.FOLLOWERS_KEY, clickedFollowers);
        otherProfileIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);

        // Dismisses the progress dialog
        progress.dismiss();

        // Starts the new activity
        startActivity(otherProfileIntent);
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (intentAction != null) {
                if (intentAction.equals(Constants.GET_COMMENTS_RESP)) {
                    ListView commentList = (ListView) findViewById(R.id.comment_list);
                    ArrayList<CommentRecord> comments = intent.getParcelableArrayListExtra(Constants.COMMENTS_KEY);
                    commentList.setAdapter(new CommentItemAdapter(context, R.layout.comment_list_item, comments));
                    if (comments != null) {
                        commentList.setSelection(comments.size());
                    }
                    addListOnClickListener();
                    progress.dismiss();
                } else if (intentAction.equals(Constants.POST_COMMENT_RESP)) {
                    progress.dismiss();
                    progress.setMessage("Updating comments...");
                    getCommentsIntent.putExtra(Constants.IDEA_ID_KEY, ideaId);
                    startService(getCommentsIntent);
                    String toastMsg = "Comment posted";
                    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();
                } else {
                    userData = intent.getStringArrayListExtra(Constants.USER_DATA_KEY);
                    if (userData != null) {
                        String clickedUserName = userData.get(0);
                        if (!clickedUserName.equals(originalUser)) {
                            visitOtherProfile();
                        }
                    }
                }
            }
        }
    }
}

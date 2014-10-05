package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.AddApprovingService;
import se.liu.ida.josfa969.tddd80.background_services.GetCommentsService;
import se.liu.ida.josfa969.tddd80.background_services.GetUserDataService;
import se.liu.ida.josfa969.tddd80.background_services.IsApprovingService;
import se.liu.ida.josfa969.tddd80.background_services.PostCommentService;
import se.liu.ida.josfa969.tddd80.background_services.RemoveApprovingService;
import se.liu.ida.josfa969.tddd80.fragments.IdeaDetailFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.item_records.CommentRecord;
import se.liu.ida.josfa969.tddd80.list_adapters.CommentItemAdapter;

public class IdeaDetailActivity extends Activity {
    // A tag of this class used by Log
    private final String ACTIVITY_TAG = "se.liu.ida.josfa969.tddd80.activities.ConversationActivity";

    // Basic data
    private String originalUser;
    private String poster;
    private String ideaText;
    private ArrayList<String> tags;
    private String tagString;
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
        Log.d(ACTIVITY_TAG, "On Create");

        // Gets data
        Intent initIntent = getIntent();
        originalUser = initIntent.getStringExtra(Constants.ORIGINAL_USER_KEY);
        poster = initIntent.getStringExtra(Constants.POSTER_KEY);
        ideaText = initIntent.getStringExtra(Constants.IDEA_TEXT_KEY);
        tags = initIntent.getStringArrayListExtra(Constants.TAG_STRING_KEY);
        tagString = initIntent.getStringExtra(Constants.TAG_STRING_KEY);
        approvalNum = initIntent.getStringExtra(Constants.APPROVAL_NUM_KEY);
        ideaId = initIntent.getStringExtra(Constants.IDEA_ID_KEY);

        // Creates an intent used to get basic user data
        getUserDataIntent = new Intent(this, GetUserDataService.class);

        // Creates the intent to get comments
        getCommentsIntent = new Intent(this, GetCommentsService.class);

        // Creates the progress dialog
        progress = new ProgressDialog(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new IdeaDetailFragment()).commit();
        }
    }

    // Adds filters to the receiver
    private void addReceiverFilters() {
        Log.d(ACTIVITY_TAG, "Add Receiver Filters");
        // Filters for the receiver
        IntentFilter getCommentsFilter = new IntentFilter(Constants.GET_COMMENTS_RESP);
        IntentFilter postCommentFilter = new IntentFilter(Constants.POST_COMMENT_RESP);
        IntentFilter getUserDataFilter = new IntentFilter(Constants.GET_USER_DATA_RESP);
        IntentFilter isApprovingFilter = new IntentFilter(Constants.IS_APPROVING_RESP);
        IntentFilter addApprovingFilter = new IntentFilter(Constants.ADD_APPROVING_RESP);
        IntentFilter removeApprovingFilter = new IntentFilter(Constants.REMOVE_APPROVING_RESP);
        getCommentsFilter.addCategory(Intent.CATEGORY_DEFAULT);
        postCommentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getUserDataFilter.addCategory(Intent.CATEGORY_DEFAULT);
        isApprovingFilter.addCategory(Intent.CATEGORY_DEFAULT);
        addApprovingFilter.addCategory(Intent.CATEGORY_DEFAULT);
        removeApprovingFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, getCommentsFilter);
        registerReceiver(receiver, postCommentFilter);
        registerReceiver(receiver, getUserDataFilter);
        registerReceiver(receiver, isApprovingFilter);
        registerReceiver(receiver, addApprovingFilter);
        registerReceiver(receiver, removeApprovingFilter);
    }

    @Override
    protected void onResume() {
        addReceiverFilters();
        super.onResume();
        Log.d(ACTIVITY_TAG, "On Resume");

        // Gets views to update
        TextView posterView = (TextView) findViewById(R.id.idea_detail_poster);
        TextView ideaTextView = (TextView) findViewById(R.id.idea_detail_idea_text);
        TextView tagsTextView = (TextView) findViewById(R.id.idea_detail_tags);
        TextView approvalNumView = (TextView) findViewById(R.id.idea_detail_approval_num);
        TextView ideaIdView = (TextView) findViewById(R.id.idea_detail_idea_id);

        // Updates the view's data
        posterView.setText(poster);
        ideaTextView.setText(ideaText);
        if (tags != null) {
            tagString = "";
            for (Object tag : tags) {
                tagString += "#" + tag + " ";
            }
        }
        tagsTextView.setText(tagString);
        approvalNumView.setText(approvalNum);
        ideaIdView.setText(ideaId);

        progress.setTitle("Loading");
        progress.setMessage("Fetching idea data...");
        progress.show();
        Intent isApprovingIntent = new Intent(this, IsApprovingService.class);
        isApprovingIntent.putExtra(Constants.USER_NAME_KEY, originalUser);
        isApprovingIntent.putExtra(Constants.IDEA_ID_KEY, ideaId);
        startService(isApprovingIntent);

        progress.setMessage("Fetching comments...");
        progress.show();
        getCommentsIntent.putExtra(Constants.IDEA_ID_KEY, ideaId);
        startService(getCommentsIntent);
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            Log.e(ACTIVITY_TAG, "Receiver not registered", e);
            e.printStackTrace();
        }
        super.onPause();
    }

    public void onCommentClick(View view) {
        Log.d(ACTIVITY_TAG, "On Comment Click");
        EditText commentInput = (EditText) findViewById(R.id.comment_input);
        String commentText = String.valueOf(commentInput.getText());

        if (commentText.equals("")) {
            String toastMsg = "You have to type something";
            Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
        } else {
            progress.setTitle("Loading");
            progress.setMessage("Posting comment...");
            progress.show();
            commentInput.setText("");
            Intent postCommentIntent = new Intent(this, PostCommentService.class);
            postCommentIntent.putExtra(Constants.USER_NAME_KEY, originalUser);
            postCommentIntent.putExtra(Constants.IDEA_ID_KEY, ideaId);
            postCommentIntent.putExtra(Constants.COMMENT_TEXT_KEY, commentText);
            startService(postCommentIntent);
        }
    }

    private void addListOnClickListener() {
        Log.d(ACTIVITY_TAG, "Add List On Click Listener");
        final ListView commentList = (ListView) findViewById(R.id.comment_list);
        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CommentRecord o = (CommentRecord) commentList.getItemAtPosition(position);
                if (o != null) {
                    if (!originalUser.equals(o.user)) {
                        progress.setTitle("Loading");
                        progress.setMessage("Fetching user data...");
                        progress.show();
                        getUserDataIntent.putExtra(Constants.USER_NAME_KEY, o.user);
                        startService(getUserDataIntent);
                    }
                }
            }
        });
    }

    public void visitOtherProfile() {
        Log.d(ACTIVITY_TAG, "Visit Other Profile");
        Intent otherProfileIntent = new Intent(this, OtherProfileActivity.class);

        // Gets basic data of the user whose idea
        // was clicked (excluding the users which
        // the user whose idea was clicked is following)
        String clickedUserName = userData.get(0);
        String clickedEMail = userData.get(1);
        String clickedCountry = userData.get(2);
        String clickedCity = userData.get(3);
        String clickedFollowers = userData.get(4);
        String clickedLocation = userData.get(5);

        // Attaches the basic data to the intent
        otherProfileIntent.putExtra(Constants.USER_NAME_KEY, clickedUserName);
        otherProfileIntent.putExtra(Constants.E_MAIL_KEY, clickedEMail);
        otherProfileIntent.putExtra(Constants.COUNTRY_KEY, clickedCountry);
        otherProfileIntent.putExtra(Constants.CITY_KEY, clickedCity);
        otherProfileIntent.putExtra(Constants.FOLLOWERS_KEY, clickedFollowers);
        otherProfileIntent.putExtra(Constants.LOCATION_KEY, clickedLocation);
        otherProfileIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);

        // Dismisses the progress dialog
        progress.dismiss();

        // Starts the new activity
        startActivity(otherProfileIntent);
    }

    public void onIdeaDetailApprovalClick(View view) {
        Log.d(ACTIVITY_TAG, "On Idea Detail Approval Click");
        // Temporarily updates approval num
        TextView approvalNum = (TextView) findViewById(R.id.idea_detail_approval_num);
        int incApprovalNum = Integer.parseInt(String.valueOf(approvalNum.getText())) + 1;
        approvalNum.setText(String.valueOf(incApprovalNum));

        // Temporarily switches the visibility of the buttons
        Button approvalButton = (Button) findViewById(R.id.idea_detail_approval_button);
        Button unApprovalButton = (Button) findViewById(R.id.idea_detail_un_approval_button);
        approvalButton.setVisibility(View.GONE);
        unApprovalButton.setVisibility(View.VISIBLE);

        // Starts a service to increase approval num
        Intent addApprovingIntent = new Intent(this, AddApprovingService.class);
        addApprovingIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
        addApprovingIntent.putExtra(Constants.IDEA_ID_KEY, ideaId);
        startService(addApprovingIntent);
    }

    public void onIdeaDetailUnApprovalClick(View view) {
        Log.d(ACTIVITY_TAG, "On Idea Detail Un-Approval Click");
        // Temporarily updates approval num
        TextView approvalNum = (TextView) findViewById(R.id.idea_detail_approval_num);
        int decApprovalNum = Integer.parseInt(String.valueOf(approvalNum.getText())) - 1;
        approvalNum.setText(String.valueOf(decApprovalNum));

        // Temporarily switches the visibility of the buttons
        Button approvalButton = (Button) findViewById(R.id.idea_detail_approval_button);
        Button unApprovalButton = (Button) findViewById(R.id.idea_detail_un_approval_button);
        approvalButton.setVisibility(View.VISIBLE);
        unApprovalButton.setVisibility(View.GONE);

        // Starts a service to increase approval num
        Intent removeApprovingIntent = new Intent(this, RemoveApprovingService.class);
        removeApprovingIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
        removeApprovingIntent.putExtra(Constants.IDEA_ID_KEY, ideaId);
        startService(removeApprovingIntent);
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(ACTIVITY_TAG, "On Receive");
            String intentAction = intent.getAction();
            if (intentAction != null) {
                if (intentAction.equals(Constants.GET_COMMENTS_RESP)) {
                    Log.d(ACTIVITY_TAG, "GET_COMMENTS_RESP");
                    ListView commentList = (ListView) findViewById(R.id.comment_list);
                    ArrayList<CommentRecord> comments = intent.getParcelableArrayListExtra(Constants.COMMENTS_KEY);
                    commentList.setAdapter(new CommentItemAdapter(context, R.layout.comment_list_item, comments));
                    if (comments != null) {
                        commentList.setSelection(comments.size());
                    }
                    addListOnClickListener();
                    progress.dismiss();
                } else if (intentAction.equals(Constants.POST_COMMENT_RESP)) {
                    Log.d(ACTIVITY_TAG, "POST_COMMENT_RESP");
                    progress.dismiss();
                    progress.setMessage("Updating comments...");
                    getCommentsIntent.putExtra(Constants.IDEA_ID_KEY, ideaId);
                    startService(getCommentsIntent);
                    String toastMsg = "Comment posted";
                    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();
                } else if (intentAction.equals(Constants.GET_USER_DATA_RESP)) {
                    Log.d(ACTIVITY_TAG, "GET_USER_DATA_RESP");
                    userData = intent.getStringArrayListExtra(Constants.USER_DATA_KEY);
                    visitOtherProfile();
                } else if (intentAction.equals(Constants.IS_APPROVING_RESP)) {
                    Log.d(ACTIVITY_TAG, "IS_APPROVING_RESP");
                    boolean isApproving = intent.getBooleanExtra(Constants.IS_APPROVING_KEY, false);
                    Button approvalButton = (Button) findViewById(R.id.idea_detail_approval_button);
                    Button unApprovalButton = (Button) findViewById(R.id.idea_detail_un_approval_button);
                    if (isApproving) {
                        Log.d(ACTIVITY_TAG, "Is Approving");
                        approvalButton.setVisibility(View.GONE);
                        unApprovalButton.setVisibility(View.VISIBLE);
                    } else {
                        Log.d(ACTIVITY_TAG, "Is Not Approving");
                        approvalButton.setVisibility(View.VISIBLE);
                        unApprovalButton.setVisibility(View.GONE);
                    }
                } else {
                    String toastMsg = intent.getStringExtra(Constants.APPROVING_TOAST_MSG_KEY);
                    Log.d(ACTIVITY_TAG, toastMsg);
                    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

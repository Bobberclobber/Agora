package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.AddFollowerService;
import se.liu.ida.josfa969.tddd80.background_services.GetOtherUserIdeasService;
import se.liu.ida.josfa969.tddd80.background_services.RemoveFollowerService;
import se.liu.ida.josfa969.tddd80.fragments.OtherProfileFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.list_adapters.IdeaItemAdapter;
import se.liu.ida.josfa969.tddd80.item_records.IdeaRecord;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

public class OtherProfileActivity extends Activity {

    // Initializes basic data variables
    private String userName = null;
    private String eMail = null;
    private String country = null;
    private String city = null;
    private String followers = null;
    private String location = null;
    private String originalUser = null;

    // An intent used to visit the detail view of an idea
    public Intent ideaDetailIntent;

    // Broadcast receiver
    private ResponseReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Creates an intent used to get basic user data
        ideaDetailIntent = new Intent(this, IdeaDetailActivity.class);

        // Add filters for the receiver
        addReceiverFilters();

        // Gets the other user's data
        getOtherUserData(savedInstanceState);
    }

    private void addReceiverFilters() {
        // Filters for the receiver
        IntentFilter addFollowerFilter = new IntentFilter(Constants.ADD_FOLLOWER_RESP);
        IntentFilter removeFollowerFilter = new IntentFilter(Constants.REMOVE_FOLLOWER_RESP);
        IntentFilter getIdeasFilter = new IntentFilter(Constants.GET_OTHER_USER_IDEAS_RESP);
        addFollowerFilter.addCategory(Intent.CATEGORY_DEFAULT);
        removeFollowerFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getIdeasFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, addFollowerFilter);
        registerReceiver(receiver, removeFollowerFilter);
        registerReceiver(receiver, getIdeasFilter);
    }

    private void getOtherUserData(Bundle savedInstanceState) {
        // Gets all data sent by the intent starting this activity
        Intent initIntent = getIntent();
        userName = initIntent.getStringExtra(Constants.USER_NAME_KEY);
        eMail = initIntent.getStringExtra(Constants.E_MAIL_KEY);
        country = initIntent.getStringExtra(Constants.COUNTRY_KEY);
        city = initIntent.getStringExtra(Constants.CITY_KEY);
        followers = initIntent.getStringExtra(Constants.FOLLOWERS_KEY);
        originalUser = initIntent.getStringExtra(Constants.ORIGINAL_USER_KEY);
        location = initIntent.getStringExtra(Constants.LOCATION_KEY);

        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        String defaultUserName = "User Name";
        String defaultEMail = "E-Mail";
        String defaultCountry = "Country";
        String defaultCity = "City";
        String defaultFollowers = "0";
        String defaultLocation = "Not Set";
        String defaultOriginalUser = "You";

        if (userName == null) {
            userName = preferences.getString(Constants.USER_NAME_KEY, defaultUserName);
        }
        if (eMail == null) {
            eMail = preferences.getString(Constants.E_MAIL_KEY, defaultEMail);
        }
        if (country == null) {
            country = preferences.getString(Constants.COUNTRY_KEY, defaultCountry);
        }
        if (city == null) {
            city = preferences.getString(Constants.CITY_KEY, defaultCity);
        }
        if (followers == null) {
            followers = preferences.getString(Constants.FOLLOWERS_KEY, defaultFollowers);
        }
        if (location == null) {
            location = preferences.getString(Constants.LOCATION_KEY, defaultLocation);
        }
        if (originalUser == null) {
            originalUser = preferences.getString(Constants.ORIGINAL_USER_KEY, defaultOriginalUser);
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new OtherProfileFragment()).commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the receiver
        unregisterReceiver(receiver);

        // When leaving this activity and starting a
        // new one, save the current user's username
        // and e-mail using a shared preference
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.USER_NAME_KEY, userName);
        editor.putString(Constants.E_MAIL_KEY, eMail);
        editor.putString(Constants.COUNTRY_KEY, country);
        editor.putString(Constants.CITY_KEY, city);
        editor.putString(Constants.FOLLOWERS_KEY, followers);
        editor.putString(Constants.LOCATION_KEY, location);
        editor.putString(Constants.ORIGINAL_USER_KEY, originalUser);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Sets the follow/un-follow buttons' visibility depending on
        // if the current user is following the other user
        Button followButton = (Button) findViewById(R.id.follow_button);
        Button unFollowButton = (Button) findViewById(R.id.un_follow_button);
        if (JsonMethods.userIsFollowing(originalUser, userName)) {
            followButton.setVisibility(View.GONE);
            unFollowButton.setVisibility(View.VISIBLE);
        } else {
            followButton.setVisibility(View.VISIBLE);
            unFollowButton.setVisibility(View.GONE);
        }

        // Gets text views
        TextView nameView = (TextView) findViewById(R.id.other_profile_name);
        TextView eMailView = (TextView) findViewById(R.id.other_profile_e_mail);
        TextView followersView = (TextView) findViewById(R.id.other_profile_follower_number);

        // Sets the text view's values
        nameView.setText(userName);
        eMailView.setText(eMail);
        followersView.setText(followers);

        Intent getOtherUserIdeasIntent = new Intent(this, GetOtherUserIdeasService.class);
        getOtherUserIdeasIntent.putExtra(Constants.USER_NAME_KEY, userName);
        startService(getOtherUserIdeasIntent);
    }

    public void onMessageClick(View view) {
        Intent conversationIntent = new Intent(this, ConversationActivity.class);
        conversationIntent.putExtra(Constants.USER_NAME_KEY, userName);
        conversationIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
        startActivity(conversationIntent);
    }

    public void onInformationClick(View view) {
        Intent informationIntent = new Intent(this, InformationActivity.class);
        informationIntent.putExtra(Constants.USER_NAME_KEY, userName);
        informationIntent.putExtra(Constants.E_MAIL_KEY, eMail);
        informationIntent.putExtra(Constants.COUNTRY_KEY, country);
        informationIntent.putExtra(Constants.CITY_KEY, city);
        informationIntent.putExtra(Constants.FOLLOWERS_KEY, followers);
        informationIntent.putExtra(Constants.LOCATION_KEY, location);
        informationIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
        startActivity(informationIntent);
    }

    public void onFollowClick(View view) {
        // Gets widgets
        Button followButton = (Button) findViewById(R.id.follow_button);
        Button unFollowButton = (Button) findViewById(R.id.un_follow_button);
        TextView followersView = (TextView) findViewById(R.id.other_profile_follower_number);

        // Temporarily increases the follower number by one
        String followerNum = String.valueOf(followersView.getText());
        int incFollowerNum = Integer.parseInt(followerNum) + 1;
        followersView.setText(String.valueOf(incFollowerNum));

        // Starts the AddFollowerService
        Intent addFollowerIntent = new Intent(this, AddFollowerService.class);
        addFollowerIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
        addFollowerIntent.putExtra(Constants.TARGET_USER_KEY, userName);
        startService(addFollowerIntent);

        // Hides the follow button and displays the un-follow button
        followButton.setVisibility(View.GONE);
        unFollowButton.setVisibility(View.VISIBLE);
    }

    public void onUnFollowClick(View view) {
        System.out.println("On Un-Follow Click");
        // Gets widgets
        Button followButton = (Button) findViewById(R.id.follow_button);
        Button unFollowButton = (Button) findViewById(R.id.un_follow_button);
        TextView followersView = (TextView) findViewById(R.id.other_profile_follower_number);

        // Temporarily decreases the follower number by one
        String followerNum = String.valueOf(followersView.getText());
        int decFollowerNum = Integer.parseInt(followerNum) - 1;
        followersView.setText(String.valueOf(decFollowerNum));

        // Starts the RemoveFollowerService
        Intent removeFollowerIntent = new Intent(this, RemoveFollowerService.class);
        removeFollowerIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
        removeFollowerIntent.putExtra(Constants.TARGET_USER_KEY, userName);
        startService(removeFollowerIntent);

        // Hides the un-follow button and displays the follow button
        unFollowButton.setVisibility(View.GONE);
        followButton.setVisibility(View.VISIBLE);
    }

    private void addListOnClickListener() {
        final ListView otherUserIdeasList = (ListView) findViewById(R.id.other_profile_recent_ideas);
        otherUserIdeasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                IdeaRecord o = (IdeaRecord) otherUserIdeasList.getItemAtPosition(position);
                if (o != null) {
                    ideaDetailIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
                    ideaDetailIntent.putExtra(Constants.POSTER_KEY, o.poster);
                    ideaDetailIntent.putExtra(Constants.IDEA_TEXT_KEY, o.ideaText);
                    ideaDetailIntent.putExtra(Constants.TAG_STRING_KEY, o.tags);
                    ideaDetailIntent.putExtra(Constants.APPROVAL_NUM_KEY, o.approvalNum);
                    ideaDetailIntent.putExtra(Constants.IDEA_ID_KEY, o.ideaId);
                    startActivity(ideaDetailIntent);
                }
            }
        });
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (intentAction != null) {
                if (intentAction.equals(Constants.GET_OTHER_USER_IDEAS_RESP)) {
                    ListView recentIdeasList = (ListView) findViewById(R.id.other_profile_recent_ideas);
                    ArrayList<IdeaRecord> recentIdeas = intent.getParcelableArrayListExtra(Constants.IDEAS_KEY);
                    if (recentIdeas != null) {
                        recentIdeasList.setAdapter(new IdeaItemAdapter(context, R.layout.idea_list_item, recentIdeas, originalUser));
                    }
                    addListOnClickListener();
                } else {
                    // Creates a toast showing that you now follow the target user
                    String toastMsg = intent.getStringExtra(Constants.FOLLOW_TOAST_MSG_KEY);
                    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

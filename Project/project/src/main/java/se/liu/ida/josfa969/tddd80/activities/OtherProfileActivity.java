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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.AddFollowerService;
import se.liu.ida.josfa969.tddd80.background_services.RemoveFollowerService;
import se.liu.ida.josfa969.tddd80.fragments.OtherProfileFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.list_adapters.IdeaItemAdapter;
import se.liu.ida.josfa969.tddd80.item_records.IdeaRecord;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

public class OtherProfileActivity extends Activity {

    // Gets strings used as keys to
    // get data sent through an intent
    private final String USER_NAME_KEY = Constants.USER_NAME_KEY;
    private final String E_MAIL_KEY = Constants.E_MAIL_KEY;
    private final String COUNTRY_KEY = Constants.COUNTRY_KEY;
    private final String CITY_KEY = Constants.CITY_KEY;
    private final String FOLLOWERS_KEY = Constants.FOLLOWERS_KEY;
    private final String ORIGINAL_USER_KEY = Constants.ORIGINAL_USER_KEY;

    // Initializes basic data variables
    private String userName = null;
    private String eMail = null;
    private String country = null;
    private String city = null;
    private String followers = null;
    private String originalUser = null;

    // Broadcast receiver
    private ResponseReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Filters for the receiver
        IntentFilter addFollowerFilter = new IntentFilter(Constants.ADD_FOLLOWER_RESP);
        IntentFilter removeFollowerFilter = new IntentFilter(Constants.REMOVE_FOLLOWER_RESP);
        addFollowerFilter.addCategory(Intent.CATEGORY_DEFAULT);
        removeFollowerFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, addFollowerFilter);
        registerReceiver(receiver, removeFollowerFilter);

        // Gets all data sent by the intent starting this activity
        Intent initIntent = getIntent();
        userName = initIntent.getStringExtra(USER_NAME_KEY);
        eMail = initIntent.getStringExtra(E_MAIL_KEY);
        country = initIntent.getStringExtra(COUNTRY_KEY);
        city = initIntent.getStringExtra(CITY_KEY);
        followers = initIntent.getStringExtra(FOLLOWERS_KEY);
        originalUser = initIntent.getStringExtra(ORIGINAL_USER_KEY);

        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        String defaultUserName = "User Name";
        String defaultEMail = "E-Mail";
        String defaultCountry = "Country";
        String defaultCity = "City";
        String defaultFollowers = "0";
        String defaultOriginalUser = "You";

        if (userName == null) {
            userName = preferences.getString(USER_NAME_KEY, defaultUserName);
        }
        if (eMail == null) {
            eMail = preferences.getString(E_MAIL_KEY, defaultEMail);
        }
        if (country == null) {
            country = preferences.getString(COUNTRY_KEY, defaultCountry);
        }
        if (city == null) {
            city = preferences.getString(CITY_KEY, defaultCity);
        }
        if (followers == null) {
            followers = preferences.getString(FOLLOWERS_KEY, defaultFollowers);
        }
        if (originalUser == null) {
            originalUser = preferences.getString(ORIGINAL_USER_KEY, defaultOriginalUser);
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new OtherProfileFragment()).commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        System.out.println("----------");
        System.out.println("On Pause");
        System.out.println("----------");

        // Unregister the receiver
        unregisterReceiver(receiver);

        // When leaving this activity and starting a
        // new one, save the current user's username
        // and e-mail using a shared preference
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_NAME_KEY, userName);
        editor.putString(E_MAIL_KEY, eMail);
        editor.putString(COUNTRY_KEY, country);
        editor.putString(CITY_KEY, city);
        editor.putString(FOLLOWERS_KEY, followers);
        editor.putString(ORIGINAL_USER_KEY, originalUser);
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

        updateRecentIdeas();
    }

    private void updateRecentIdeas() {
        ListView recentIdeasList = (ListView) findViewById(R.id.other_profile_recent_ideas);
        ArrayList<IdeaRecord> recentIdeas = JsonMethods.getOtherUserRecentIdeas(userName);
        if (recentIdeas == null) {
            System.out.println("Error!");
        } else if (recentIdeas.isEmpty()) {
            System.out.println("No Recent Events");
        } else {
            recentIdeasList.setAdapter(new IdeaItemAdapter(this, R.layout.idea_list_item, recentIdeas, originalUser));
        }
    }

    public void onMessageClick(View view) {
        Intent conversationIntent = new Intent(this, ConversationActivity.class);
        conversationIntent.putExtra(USER_NAME_KEY, userName);
        conversationIntent.putExtra(ORIGINAL_USER_KEY, originalUser);
        startActivity(conversationIntent);
    }

    public void onInformationClick(View view) {
        Intent informationIntent = new Intent(this, InformationActivity.class);
        informationIntent.putExtra(Constants.USER_NAME_KEY, userName);
        informationIntent.putExtra(Constants.E_MAIL_KEY, eMail);
        informationIntent.putExtra(Constants.COUNTRY_KEY, country);
        informationIntent.putExtra(Constants.CITY_KEY, city);
        informationIntent.putExtra(Constants.FOLLOWERS_KEY, followers);
        informationIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
        startActivity(informationIntent);
    }

    public void onFollowClick(View view) {
        System.out.println("On Follow Click");
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
        addFollowerIntent.putExtra(ORIGINAL_USER_KEY, originalUser);
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
        removeFollowerIntent.putExtra(ORIGINAL_USER_KEY, originalUser);
        removeFollowerIntent.putExtra(Constants.TARGET_USER_KEY, userName);
        startService(removeFollowerIntent);

        // Hides the un-follow button and displays the follow button
        unFollowButton.setVisibility(View.GONE);
        followButton.setVisibility(View.VISIBLE);
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("----------");
            System.out.println("On Receive");
            System.out.println("----------");

            // Creates a toast showing that you now follow the target user
            String toastMsg = intent.getStringExtra(Constants.FOLLOW_TOAST_MSG_KEY);
            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();
        }
    }
}

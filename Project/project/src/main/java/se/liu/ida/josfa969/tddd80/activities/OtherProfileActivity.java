package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.fragments.OtherProfileFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.IdeaItemAdapter;
import se.liu.ida.josfa969.tddd80.help_classes.IdeaRecord;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

public class OtherProfileActivity extends Activity {

    // Gets strings used as keys to
    // get data sent through an intent
    private String userNameKey = Constants.USER_NAME_KEY;
    private String eMailKey = Constants.E_MAIL_KEY;
    private String countryKey = Constants.COUNTRY_KEY;
    private String cityKey = Constants.CITY_KEY;
    private String followersKey = Constants.FOLLOWERS_KEY;
    private String originalUserKey = Constants.ORIGINAL_USER_KEY;

    // Initializes basic data variables
    private String userName = null;
    private String eMail = null;
    private String country = null;
    private String city = null;
    private String followers = null;
    private String originalUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Gets all data sent by the intent starting this activity
        Intent initIntent = getIntent();
        userName = initIntent.getStringExtra(userNameKey);
        eMail = initIntent.getStringExtra(eMailKey);
        country = initIntent.getStringExtra(countryKey);
        city = initIntent.getStringExtra(cityKey);
        followers = initIntent.getStringExtra(followersKey);
        originalUser = initIntent.getStringExtra(originalUserKey);

        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        String defaultUserName = "User Name";
        String defaultEMail = "E-Mail";
        String defaultCountry = "Country";
        String defaultCity = "City";
        String defaultFollowers = "0";
        String defaultOriginalUser = "You";

        if (userName == null) {
            userName = preferences.getString(userNameKey, defaultUserName);
        }
        if (eMail == null) {
            eMail = preferences.getString(eMailKey, defaultEMail);
        }
        if (country == null) {
            country = preferences.getString(countryKey, defaultCountry);
        }
        if (city == null) {
            city = preferences.getString(cityKey, defaultCity);
        }
        if (followers == null) {
            followers = preferences.getString(followersKey, defaultFollowers);
        }
        if (originalUser == null) {
            originalUser = preferences.getString(originalUserKey, defaultOriginalUser);
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

        // When leaving this activity and starting a
        // new one, save the current user's username
        // and e-mail using a shared preference
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(userNameKey, userName);
        editor.putString(eMailKey, eMail);
        editor.putString(countryKey, country);
        editor.putString(cityKey, city);
        editor.putString(followersKey, followers);
        editor.putString(originalUserKey, originalUser);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

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
            recentIdeasList.setAdapter(new IdeaItemAdapter(this, R.layout.idea_list_item, recentIdeas));
        }
    }

    public void onMessageClick(View view) {
        Intent conversationIntent = new Intent(this, ConversationActivity.class);
        conversationIntent.putExtra(userNameKey, userName);
        conversationIntent.putExtra(originalUserKey, originalUser);
        startActivity(conversationIntent);
    }

    public void onInformationClick(View view) {
    }

    public void onFollowClick(View view) {
        Button followButton = (Button) findViewById(R.id.follow_button);
        followButton.setEnabled(false);
    }
}

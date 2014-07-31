package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.fragments.ConversationFragment;
import se.liu.ida.josfa969.tddd80.fragments.SendMessageFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * An activity displaying a conversation between two users.
 * The following data should be attached
 * to the intent starting this activity:
 *
 * USER_NAME_KEY - The user name of the user not using the current instance of the application
 * ORIGINAL_USER_KEY - The user name of the user currently using the application
 */
public class ConversationActivity extends Activity {

    // Gets strings used as keys to
    // get data sent through an intent
    private String userNameKey = Constants.USER_NAME_KEY;
    private String originalUserKey = Constants.ORIGINAL_USER_KEY;

    // Initializes basic data variables
    private String userName = null;
    private String originalUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // Gets all data sent by the intent starting this activity
        Intent initIntent = getIntent();
        userName = initIntent.getStringExtra(userNameKey);
        originalUser = initIntent.getStringExtra(originalUserKey);

        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        String defaultUserName = "User Name";
        String defaultOriginalUser = "You";

        if (userName == null) {
            userName = preferences.getString(userNameKey, defaultUserName);
        }
        if (originalUser == null) {
            originalUser = preferences.getString(originalUserKey, defaultOriginalUser);
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new ConversationFragment()).commit();
            getFragmentManager().beginTransaction().add(R.id.container, new SendMessageFragment()).commit();
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
        editor.putString(originalUserKey, originalUser);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView conversationPartnerName = (TextView) findViewById(R.id.conversation_partner_name);
        conversationPartnerName.setText(userName);

        ListView messagesList = (ListView) findViewById(R.id.messages_list);
    }

    public void onProfileImageClick(View view) {
        // Gets required key strings
        String eMailKey = Constants.E_MAIL_KEY;
        String countryKey = Constants.COUNTRY_KEY;
        String cityKey = Constants.CITY_KEY;
        String followersKey = Constants.FOLLOWERS_KEY;

        // Gets the user's data
        ArrayList<String> userData = JsonMethods.getUserData(userName);
        String eMail = userData.get(1);
        String country = userData.get(2);
        String city = userData.get(3);
        String followers = userData.get(4);

        Intent otherProfileIntent = new Intent(this, OtherProfileActivity.class);

        // Attaches the basic data to the intent
        otherProfileIntent.putExtra(userNameKey, userName);
        otherProfileIntent.putExtra(eMailKey, eMail);
        otherProfileIntent.putExtra(countryKey, country);
        otherProfileIntent.putExtra(cityKey, city);
        otherProfileIntent.putExtra(followersKey, followers);
        otherProfileIntent.putExtra(originalUserKey, userName);

        // Starts the new activity
        startActivity(otherProfileIntent);
    }
}

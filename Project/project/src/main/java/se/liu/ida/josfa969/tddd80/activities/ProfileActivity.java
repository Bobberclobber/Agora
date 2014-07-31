package se.liu.ida.josfa969.tddd80.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.IdeaItemAdapter;
import se.liu.ida.josfa969.tddd80.help_classes.IdeaRecord;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.help_classes.ProfilePagerAdapter;
import se.liu.ida.josfa969.tddd80.R;

public class ProfileActivity extends FragmentActivity {
    // When requested, this adapter returns one of the
    // following different Fragments: IdeaFeedFragment,
    // PostIdeaFragment, FindFragment or ProfileSettingsFragment
    // depending on which number is swiped to
    ProfilePagerAdapter pagerAdapter;
    ViewPager profilePager;
    ViewPager.SimpleOnPageChangeListener onPageChangeListener;

    private String userName = null;
    private String eMail = null;
    private String[] tabTitleList = {"Post", "Idea Feed", "Messages", "Find", "Settings"};

    private String userNameKey = Constants.USER_NAME_KEY;
    private String eMailKey = Constants.E_MAIL_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        System.out.println("----------");
        System.out.println("On Create");
        System.out.println("----------");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Gets the users user name and e-mail address
        Intent initIntent = getIntent();
        userName = initIntent.getStringExtra(userNameKey);
        eMail = initIntent.getStringExtra(eMailKey);

        System.out.println("Post Get Intent");
        System.out.println("User Name: " + userName);
        System.out.println("E-Mail: " + eMail);

        // If using an intent to get the user name and
        // e-mail results in null-values, use the values
        // saved in the external file to get these values
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        String defaultUserName = "User Name";
        String defaultEMail = "E-Mail";
        if (userName == null) {
            userName = preferences.getString(userNameKey, defaultUserName);
        }
        if (eMail == null) {
            eMail = preferences.getString(eMailKey, defaultEMail);
        }

        System.out.println("Post Preferences");
        System.out.println("User Name: " + userName);
        System.out.println("E-Mail: " + eMail);

        // Add tabs to the action bar
        final ActionBar actionBar = getActionBar();

        // Creates the page change listener
        onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // When swiping between pages, select the corresponding tab
                getActionBar().setSelectedNavigationItem(position);
                System.out.println("Current Item: " + pagerAdapter.getItem(position));
                System.out.println("Position: " + position);
                if (position == 1) {
                    addListOnClickListener();
                    updateRecentEvents();
                } else if (position == 4) {
                    TextView profileUserName = (TextView) findViewById(R.id.profile_user_name);
                    TextView profileEMail = (TextView) findViewById(R.id.profile_e_mail);
                    profileUserName.setText(userName);
                    profileEMail.setText(eMail);
                }
            }
        };

        // ViewPager and its adapter use support library fragments,
        // so use getSupportFragmentManager
        pagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager());
        profilePager = ((ViewPager) findViewById(R.id.profile_pager));
        profilePager.setAdapter(pagerAdapter);
        profilePager.setOnPageChangeListener(onPageChangeListener);

        // Specify that tabs should be displayed in the action bar
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }

        // Create a tab listener that is called when the user changes tabs
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                // When the tab is selected, switch to the corresponding tab in the ViewPager
                profilePager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }
        };

        // Add the tabs
        if (actionBar != null) {
            for (String tabTitle : tabTitleList) {
                ActionBar.Tab tempTab = actionBar.newTab();
                tempTab.setText(tabTitle);
                tempTab.setTabListener(tabListener);
                actionBar.addTab(tempTab);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        System.out.println("----------");
        System.out.println("On Pause");
        System.out.println("----------");

        System.out.println(userNameKey + " - " + userName);
        System.out.println(eMailKey + " - " + eMail);

        // When leaving this activity and starting a
        // new one, save the current user's username
        // and e-mail using a shared preference
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(userNameKey, userName);
        editor.putString(eMailKey, eMail);
        editor.commit();
    }

    private void updateRecentEvents() {
        ListView recentIdeasList = (ListView) findViewById(R.id.recent_ideas);
        ArrayList<IdeaRecord> recentIdeas = JsonMethods.getRecentIdeas(userName);
        if (recentIdeas == null) {
            System.out.println("Error!");
        } else if (recentIdeas.isEmpty()) {
            System.out.println("No Recent Events");
        } else {
            recentIdeasList.setAdapter(new IdeaItemAdapter(this, R.layout.idea_list_item, recentIdeas));
        }
    }

    private void addListOnClickListener() {
        final ListView recentIdeasList = (ListView) findViewById(R.id.recent_ideas);
        recentIdeasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                IdeaRecord o = (IdeaRecord) recentIdeasList.getItemAtPosition(position);
                if (o != null) {
                    ArrayList<String> userData = JsonMethods.getUserData(o.poster);
                    String clickedUserName = userData.get(0);
                    if (!clickedUserName.equals(userName)) {
                        onListItemClick(userData);
                    }
                }
            }
        });
    }

    public void onListItemClick(ArrayList<String> userData) {
        Intent otherProfileIntent = new Intent(this, OtherProfileActivity.class);

        // Gets the keys not already provided by the activity
        String countryKey = Constants.COUNTRY_KEY;
        String cityKey = Constants.CITY_KEY;
        String followersKey = Constants.FOLLOWERS_KEY;
        String originalUserKey = Constants.ORIGINAL_USER_KEY;

        // Gets basic data of the user whose idea
        // was clicked (excluding the users which
        // the user whose idea was clicked is following)
        String clickedUserName = userData.get(0);
        String clickedEMail = userData.get(1);
        String clickedCountry = userData.get(2);
        String clickedCity = userData.get(3);
        String clickedFollowers = userData.get(4);

        // Attaches the basic data to the intent
        otherProfileIntent.putExtra(userNameKey, clickedUserName);
        otherProfileIntent.putExtra(eMailKey, clickedEMail);
        otherProfileIntent.putExtra(countryKey, clickedCountry);
        otherProfileIntent.putExtra(cityKey, clickedCity);
        otherProfileIntent.putExtra(followersKey, clickedFollowers);
        otherProfileIntent.putExtra(originalUserKey, userName);

        // Starts the new activity
        startActivity(otherProfileIntent);
    }

    public void onPostClick(View view) {
        // Get the inputs
        EditText ideaTextInput = (EditText) findViewById(R.id.idea_input);

        // Get the status text views
        TextView positiveStatusText = (TextView) findViewById(R.id.positive_post_status_text);
        TextView negativeStatusText = (TextView) findViewById(R.id.negative_post_status_text);

        // Get the contents of the inputs
        String ideaText = String.valueOf(ideaTextInput.getText());

        if (ideaText.equals("")) {
            positiveStatusText.setText("");
            negativeStatusText.setText("You have to write something");
        } else {
            // Create and show the loading spinner
            ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait While Posting Idea...");
            progress.show();

            // Gets the JSON response from the given input
            String response = JsonMethods.postIdea(ideaText, userName);
            if (response.equals("Success")) {
                progress.dismiss();
                ideaTextInput.setText("");
                positiveStatusText.setText("Idea Posted");
                negativeStatusText.setText("");
            } else {
                progress.dismiss();
                positiveStatusText.setText("");
                negativeStatusText.setText("Something went wrong");
            }
        }
    }

    public void onUpdateClick(View view) {
    }
}

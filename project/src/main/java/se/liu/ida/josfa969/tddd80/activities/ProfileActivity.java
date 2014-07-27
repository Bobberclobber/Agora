package se.liu.ida.josfa969.tddd80.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.IdeaRecord;
import se.liu.ida.josfa969.tddd80.JsonMethods;
import se.liu.ida.josfa969.tddd80.ProfilePagerAdapter;
import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.fragments.ProfileFragment;

public class ProfileActivity extends FragmentActivity {
    // When requested, this adapter returns one of the
    // following different Fragments: ProfileFragment,
    // PostIdeaFragment, FindFragment or ProfileSettingsFragment
    // depending on which number is swiped to
    ProfilePagerAdapter pagerAdapter;
    ViewPager profilePager;

    private String identifier = "";
    private String[] tabTitleList = {"Post", "Recent", "Find", "Settings"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Gets the users identifier (either an e-mail address or a username)
        identifier = getIntent().getStringExtra("identifier");

        // Add tabs to the action bar
        final ActionBar actionBar = getActionBar();

        // ViewPager and its adapter use support library fragments,
        // so use getSupportFragmentManager
        pagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager());
        profilePager = ((ViewPager) findViewById(R.id.profile_pager));
        profilePager.setAdapter(pagerAdapter);
        profilePager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        // When swiping between pages, select the corresponding tab
                        getActionBar().setSelectedNavigationItem(position);
                        System.out.println(pagerAdapter.getRegisteredFragment(position));
                        if (position == 1) {
                            updateRecentEvents();
                        }
                    }
                }
        );

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

    public void updateRecentEvents() {
        ListView recentEventsList = (ListView) findViewById(R.id.recent_events);
        ArrayList<IdeaRecord> recentEvents = JsonMethods.getRecentEvents(identifier);
        System.out.println(recentEvents);
        if (recentEvents.isEmpty()) {
            System.out.println("No Recent Events");
        } else {
            recentEventsList.setAdapter(new IdeaItemAdapter(this, R.layout.idea_list_item, recentEvents));
        }
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
            String response = JsonMethods.postIdea(ideaText, identifier);
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
}

package se.liu.ida.josfa969.tddd80.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.GetIdeaFeedService;
import se.liu.ida.josfa969.tddd80.background_services.GetUserDataService;
import se.liu.ida.josfa969.tddd80.background_services.PostIdeaService;
import se.liu.ida.josfa969.tddd80.background_services.UpdateUserDataService;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.ProfilePagerAdapter;
import se.liu.ida.josfa969.tddd80.item_records.IdeaRecord;
import se.liu.ida.josfa969.tddd80.list_adapters.IdeaItemAdapter;

public class ProfileActivity extends FragmentActivity {
    // When requested, this adapter returns one of the
    // following different Fragments: IdeaFeedFragment,
    // PostIdeaFragment, FindFragment or ProfileSettingsFragment
    // depending on which number is swiped to
    ProfilePagerAdapter pagerAdapter;
    ViewPager profilePager;
    ViewPager.SimpleOnPageChangeListener onPageChangeListener;

    // The user data used when fetching data
    private String userName = null;
    private String eMail = null;
    private String password = null;
    private String country = null;
    private String city = null;

    // The user data from the updated user settings
    private String newUserName;
    private String newEMail;
    private String newPassword;
    private String newCountry;
    private String newCity;

    // A list of tab titles
    private String[] tabTitleList = {"Post", "Idea Feed", "Messages", "Find", "Settings"};

    // An intent used to visit the detail view of an idea
    public Intent ideaDetailIntent;

    // Broadcast receiver
    private ResponseReceiver receiver;

    // Progress dialog
    public ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Gets the users user name and e-mail address
        Intent initIntent = getIntent();
        userName = initIntent.getStringExtra(Constants.USER_NAME_KEY);
        eMail = initIntent.getStringExtra(Constants.E_MAIL_KEY);
        password = initIntent.getStringExtra(Constants.PASSWORD_KEY);
        country = initIntent.getStringExtra(Constants.COUNTRY_KEY);
        city = initIntent.getStringExtra(Constants.CITY_KEY);

        // Creates an intent used to get basic user data
        ideaDetailIntent = new Intent(this, IdeaDetailActivity.class);

        // If using an intent to get the user name and
        // e-mail results in null-values, use the values
        // saved in the external file to get these values
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        String defaultUserName = "User Name";
        String defaultEMail = "E-Mail";
        String defaultPassword = "Password";
        String defaultCountry = "Country";
        String defaultCity = "City";
        if (userName == null) {
            userName = preferences.getString(Constants.USER_NAME_KEY, defaultUserName);
        }
        if (eMail == null) {
            eMail = preferences.getString(Constants.E_MAIL_KEY, defaultEMail);
        }
        if (password == null) {
            password = preferences.getString(Constants.PASSWORD_KEY, defaultPassword);
        }
        if (country == null) {
            country = preferences.getString(Constants.COUNTRY_KEY, defaultCountry);
        }
        if (city == null) {
            city = preferences.getString(Constants.CITY_KEY, defaultCity);
        }

        // Add tabs to the action bar
        final ActionBar actionBar = getActionBar();

        // Creates the progress dialog
        progress = new ProgressDialog(this);

        // Creates the page change listener
        onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // When swiping between pages, select the corresponding tab
                getActionBar().setSelectedNavigationItem(position);
                if (position == 1) {
                    // Displays the progress dialog
                    progress.setTitle("Loading");
                    progress.setMessage("Fetching Idea Feed...");
                    progress.show();

                    // Starts the get idea feed service
                    startIdeaFeedService();
                } else if (position == 4) {
                    // Gets views
                    EditText profileUserName = (EditText) findViewById(R.id.profile_user_name);
                    EditText profileEMail = (EditText) findViewById(R.id.profile_e_mail);
                    EditText profilePassword = (EditText) findViewById(R.id.profile_password);
                    EditText profileCountry = (EditText) findViewById(R.id.profile_country);
                    EditText profileCity = (EditText) findViewById(R.id.profile_city);

                    // Sets the text of the views
                    profileUserName.setText(userName);
                    profileEMail.setText(eMail);
                    profilePassword.setText(password);
                    profileCountry.setText(country);
                    profileCity.setText(city);
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

        // Filters for the receiver
        IntentFilter updateUserDataFilter = new IntentFilter(Constants.UPDATE_USER_DATA_RESP);
        IntentFilter addApprovingFilter = new IntentFilter(Constants.ADD_APPROVING_RESP);
        IntentFilter removeApprovingFilter = new IntentFilter(Constants.REMOVE_APPROVING_RESP);
        IntentFilter getIdeaFeedFilter = new IntentFilter(Constants.GET_IDEA_FEED_RESP);
        IntentFilter postIdeaFilter = new IntentFilter(Constants.POST_IDEA_RESP);
        IntentFilter getUserDataFilter = new IntentFilter(Constants.GET_USER_DATA_RESP);
        updateUserDataFilter.addCategory(Intent.CATEGORY_DEFAULT);
        addApprovingFilter.addCategory(Intent.CATEGORY_DEFAULT);
        removeApprovingFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getIdeaFeedFilter.addCategory(Intent.CATEGORY_DEFAULT);
        postIdeaFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getUserDataFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, updateUserDataFilter);
        registerReceiver(receiver, addApprovingFilter);
        registerReceiver(receiver, removeApprovingFilter);
        registerReceiver(receiver, getIdeaFeedFilter);
        registerReceiver(receiver, postIdeaFilter);
        registerReceiver(receiver, getUserDataFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // When leaving this activity and starting a
        // new one, save the current user's username
        // and e-mail using a shared preference
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.USER_NAME_KEY, userName);
        editor.putString(Constants.E_MAIL_KEY, eMail);
        editor.putString(Constants.PASSWORD_KEY, password);
        editor.putString(Constants.COUNTRY_KEY, country);
        editor.putString(Constants.CITY_KEY, city);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void startIdeaFeedService() {
        Intent getIdeaFeedIntent = new Intent(this, GetIdeaFeedService.class);
        getIdeaFeedIntent.putExtra(Constants.USER_NAME_KEY, userName);
        startService(getIdeaFeedIntent);
    }

    private void addIdeasListClickListener() {
        final ListView recentIdeasList = (ListView) findViewById(R.id.recent_ideas);
        recentIdeasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                IdeaRecord o = (IdeaRecord) recentIdeasList.getItemAtPosition(position);
                if (o != null) {
                    ideaDetailIntent.putExtra(Constants.ORIGINAL_USER_KEY, userName);
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

    public void onPostClick(View view) {
        // Get the inputs
        EditText ideaTextInput = (EditText) findViewById(R.id.idea_input);
        EditText tagInput = (EditText) findViewById(R.id.tag_input);

        // Get the status text views
        TextView positiveStatusText = (TextView) findViewById(R.id.positive_post_status_text);
        TextView negativeStatusText = (TextView) findViewById(R.id.negative_post_status_text);

        // Get the contents of the inputs
        String ideaText = String.valueOf(ideaTextInput.getText());
        String tags = String.valueOf(tagInput.getText());

        if (ideaText.equals("")) {
            positiveStatusText.setText("");
            negativeStatusText.setText("You have to write something");
        } else {

            progress.setTitle("Loading");
            progress.setMessage("Posting idea...");
            progress.show();
            Intent postIdeaIntent = new Intent(this, PostIdeaService.class);
            postIdeaIntent.putExtra(Constants.IDEA_TEXT_KEY, ideaText);
            postIdeaIntent.putExtra(Constants.USER_NAME_KEY, userName);
            postIdeaIntent.putExtra(Constants.TAG_STRING_KEY, tags);
            startService(postIdeaIntent);
        }
    }

    public void onUpdateClick(View view) {
        // Gets views
        EditText profileUserName = (EditText) findViewById(R.id.profile_user_name);
        EditText profileEMail = (EditText) findViewById(R.id.profile_e_mail);
        EditText profilePassword = (EditText) findViewById(R.id.profile_password);
        EditText profileCountry = (EditText) findViewById(R.id.profile_country);
        EditText profileCity = (EditText) findViewById(R.id.profile_city);

        // Gets input
        newUserName = String.valueOf(profileUserName.getText());
        newEMail = String.valueOf(profileEMail.getText());
        newPassword = String.valueOf(profilePassword.getText());
        newCountry = String.valueOf(profileCountry.getText());
        newCity = String.valueOf(profileCity.getText());

        // Starts the progress dialog
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Updating user data...");
        progress.show();

        // Creates the intent, add extras and start the service
        Intent updateUserDataIntent = new Intent(this, UpdateUserDataService.class);
        updateUserDataIntent.putExtra(Constants.ORIGINAL_USER_KEY, userName);
        updateUserDataIntent.putExtra(Constants.ORIGINAL_E_MAIL_KEY, eMail);
        updateUserDataIntent.putExtra(Constants.USER_NAME_KEY, newUserName);
        updateUserDataIntent.putExtra(Constants.E_MAIL_KEY, newEMail);
        updateUserDataIntent.putExtra(Constants.PASSWORD_KEY, newPassword);
        updateUserDataIntent.putExtra(Constants.COUNTRY_KEY, newCountry);
        updateUserDataIntent.putExtra(Constants.CITY_KEY, newCity);
        startService(updateUserDataIntent);
    }

    public void onSearchPeopleClick(View view) {
        Intent searchPeopleIntent = new Intent(this, SearchPeopleActivity.class);
        searchPeopleIntent.putExtra(Constants.ORIGINAL_USER_KEY, userName);
        startActivity(searchPeopleIntent);
    }

    public void onSearchIdeasClick(View view) {
        Intent searchIdeasIntent = new Intent(this, SearchIdeasActivity.class);
        searchIdeasIntent.putExtra(Constants.ORIGINAL_USER_KEY, userName);
        startActivity(searchIdeasIntent);
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (intentAction != null) {
                if (intentAction.equals(Constants.ADD_APPROVING_RESP) ||
                        intentAction.equals(Constants.REMOVE_APPROVING_RESP)) {
                    String toastMsg = intent.getStringExtra(Constants.APPROVING_TOAST_MSG_KEY);
                    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();
                } else if (intentAction.equals(Constants.GET_IDEA_FEED_RESP)) {
                    ListView recentIdeasList = (ListView) findViewById(R.id.recent_ideas);
                    ArrayList<IdeaRecord> recentIdeas = intent.getParcelableArrayListExtra(Constants.IDEAS_KEY);
                    if (recentIdeas == null) {
                        System.out.println("Error!");
                    } else if (recentIdeas.isEmpty()) {
                        System.out.println("No Recent Events");
                    } else {
                        recentIdeasList.setAdapter(new IdeaItemAdapter(context, R.layout.idea_list_item, recentIdeas, userName));
                    }
                    addIdeasListClickListener();
                    progress.dismiss();
                } else if(intentAction.equals(Constants.POST_IDEA_RESP)) {
                    String response = intent.getStringExtra(Constants.RESPONSE_KEY);
                    TextView positiveStatusText = (TextView) findViewById(R.id.positive_post_status_text);
                    TextView negativeStatusText = (TextView) findViewById(R.id.negative_post_status_text);
                    EditText ideaTextInput = (EditText) findViewById(R.id.idea_input);
                    EditText tagInput = (EditText) findViewById(R.id.tag_input);

                    if (response == null) {
                        positiveStatusText.setText("");
                        negativeStatusText.setText("Something went wrong");
                    } else if (response.equals("Success")) {
                        ideaTextInput.setText("");
                        tagInput.setText("");
                        positiveStatusText.setText("Idea Posted");
                        negativeStatusText.setText("");
                    } else {
                        positiveStatusText.setText("");
                        negativeStatusText.setText("Something went wrong");
                    }

                    progress.dismiss();
                } else {
                    // Gets the response message
                    String resp = intent.getStringExtra(Constants.USER_DATA_UPDATE_MSG_KEY);
                    TextView negativeStatusText = (TextView) findViewById(R.id.negative_settings_status_text);
                    TextView positiveStatusText = (TextView) findViewById(R.id.positive_settings_status_text);
                    if (resp == null) {
                        positiveStatusText.setText("");
                        negativeStatusText.setText("Something went wrong");
                    } else if (!resp.equals("Success")) {
                        positiveStatusText.setText("");
                        negativeStatusText.setText(resp);
                    } else {
                        positiveStatusText.setText(resp);
                        negativeStatusText.setText("");
                        userName = newUserName;
                        eMail = newEMail;
                        password = newPassword;
                        country = newCountry;
                        city = newCity;
                    }

                    // Dismiss the progress dialog
                    progress.dismiss();
                }
            }
        }
    }
}

package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.GetUserDataService;
import se.liu.ida.josfa969.tddd80.background_services.IsFollowingService;
import se.liu.ida.josfa969.tddd80.background_services.SearchPeopleService;
import se.liu.ida.josfa969.tddd80.fragments.FoundPeopleFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.UserRecord;
import se.liu.ida.josfa969.tddd80.list_adapters.UserItemAdapter;

public class FoundPeopleActivity extends Activity {
    // A tag of this class used by Log
    private final String ACTIVITY_TAG = "se.liu.ida.josfa969.tddd80.activities.FoundPeopleActivity";

    // Basic data
    private String identifierString;
    private String originalUser;

    // An intent to get user data
    Intent getUserDataIntent;

    // An intent to check if the user is following the clicked user
    Intent isFollowingIntent;

    // Broadcast receiver
    private ResponseReceiver receiver;

    // Progress display
    public ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);
        Log.d(ACTIVITY_TAG, "On Create");

        Intent initIntent = getIntent();
        identifierString = initIntent.getStringExtra(Constants.IDENTIFIER_STRING_KEY);
        originalUser = initIntent.getStringExtra(Constants.ORIGINAL_USER_KEY);

        // Creates intents
        getUserDataIntent = new Intent(this, GetUserDataService.class);
        isFollowingIntent = new Intent(this, IsFollowingService.class);

        // Creates the progress dialog
        progress = new ProgressDialog(this);
        progress.dismiss();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new FoundPeopleFragment()).commit();
        }
    }

    // Adds filters to the receiver
    private void addReceiverFilters() {
        Log.d(ACTIVITY_TAG, "Add Receiver Filters");
        // Filters for the receiver
        IntentFilter searchPeopleFilter = new IntentFilter(Constants.SEARCH_PEOPLE_RESP);
        IntentFilter getUserDataFilter = new IntentFilter(Constants.GET_USER_DATA_RESP);
        IntentFilter isFollowingFilter = new IntentFilter(Constants.IS_FOLLOWING_RESP);
        searchPeopleFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getUserDataFilter.addCategory(Intent.CATEGORY_DEFAULT);
        isFollowingFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, searchPeopleFilter);
        registerReceiver(receiver, getUserDataFilter);
        registerReceiver(receiver, isFollowingFilter);
    }

    @Override
    protected void onResume() {
        addReceiverFilters();
        super.onResume();
        Log.d(ACTIVITY_TAG, "On Resume");

        // Shows the progress dialog
        progress.setTitle("Loading");
        progress.setMessage("Searching for people...");
        progress.show();

        // Starts the search people service
        Intent searchPeopleIntent = new Intent(this, SearchPeopleService.class);
        searchPeopleIntent.putExtra(Constants.IDENTIFIER_STRING_KEY, identifierString);
        startService(searchPeopleIntent);
    }

    @Override
    protected void onPause() {
        // Unregister the receiver
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            Log.e("FoundPeopleActivity", "Receiver not registered", e);
            e.printStackTrace();
        }
        Log.d(ACTIVITY_TAG, "On Pause");
        super.onPause();
    }

    private void addListOnClickListener() {
        Log.d(ACTIVITY_TAG, "Add On Click Listener");
        final ListView foundPeopleList = (ListView) findViewById(R.id.found_people_list);
        foundPeopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                progress.setTitle("Loading");
                progress.setMessage("Fetching user data...");
                progress.show();
                UserRecord o = (UserRecord) foundPeopleList.getItemAtPosition(position);
                if (o != null) {
                    getUserDataIntent.putExtra(Constants.USER_NAME_KEY, o.userName);
                    startService(getUserDataIntent);
                }
            }
        });
    }

    public void onListItemClick(ArrayList<String> userData, boolean isFollowing) {
        Log.d(ACTIVITY_TAG, "On List Item Click");
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
        String clickedAvatarImage = userData.get(6);

        // Attaches the basic data to the intent
        otherProfileIntent.putExtra(Constants.USER_NAME_KEY, clickedUserName);
        otherProfileIntent.putExtra(Constants.E_MAIL_KEY, clickedEMail);
        otherProfileIntent.putExtra(Constants.COUNTRY_KEY, clickedCountry);
        otherProfileIntent.putExtra(Constants.CITY_KEY, clickedCity);
        otherProfileIntent.putExtra(Constants.FOLLOWERS_KEY, clickedFollowers);
        otherProfileIntent.putExtra(Constants.LOCATION_KEY, clickedLocation);
        otherProfileIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
        otherProfileIntent.putExtra(Constants.IS_FOLLOWING_KEY, isFollowing);
        otherProfileIntent.putExtra(Constants.AVATAR_IMAGE_KEY, clickedAvatarImage);

        // Dismisses the progress dialog
        progress.dismiss();

        // Starts the new activity
        startActivity(otherProfileIntent);
    }

    private class ResponseReceiver extends BroadcastReceiver {
        private ArrayList<String> userData;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(ACTIVITY_TAG, "On Receive");
            String intentAction = intent.getAction();
            if (intentAction != null) {
                if (intentAction.equals(Constants.SEARCH_PEOPLE_RESP)) {
                    Log.d(ACTIVITY_TAG, "SEARCH_PEOPLE_RESP");
                    // Gets the array list of user records from the broadcast intent
                    ArrayList<UserRecord> people = intent.getParcelableArrayListExtra(Constants.PEOPLE_KEY);
                    // Gets status text view
                    TextView statusText = (TextView) findViewById(R.id.found_people_status_text);
                    if (people == null) {
                        statusText.setText("No Users Found");
                    } else if (people.isEmpty()) {
                        statusText.setText("No Users Found");
                    } else {
                        ListView foundPeopleList = (ListView) findViewById(R.id.found_people_list);
                        foundPeopleList.setAdapter(new UserItemAdapter(context, R.layout.user_list_item, people));
                    }
                    // Dismisses the progress dialog
                    progress.dismiss();
                    addListOnClickListener();
                } else if (intentAction.equals(Constants.GET_USER_DATA_RESP)) {
                    Log.d(ACTIVITY_TAG, "GET_USER_DATA_RESP");
                    userData = intent.getStringArrayListExtra(Constants.USER_DATA_KEY);
                    if (userData != null) {
                        String clickedUserName = userData.get(0);
                        if (!clickedUserName.equals(originalUser)) {
                            isFollowingIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
                            isFollowingIntent.putExtra(Constants.USER_NAME_KEY, clickedUserName);
                            startService(isFollowingIntent);
                        }
                    }
                } else if (intentAction.equals(Constants.IS_FOLLOWING_RESP)) {
                    Log.d(ACTIVITY_TAG, "IS_FOLLOWING_RESP");
                    onListItemClick(userData, intent.getBooleanExtra(Constants.IS_FOLLOWING_KEY, false));
                }
            }
        }
    }
}

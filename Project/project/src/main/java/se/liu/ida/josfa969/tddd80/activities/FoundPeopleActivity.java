package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.SearchPeopleService;
import se.liu.ida.josfa969.tddd80.fragments.FoundPeopleFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.UserRecord;
import se.liu.ida.josfa969.tddd80.list_adapters.UserItemAdapter;

public class FoundPeopleActivity extends Activity {
    private String identifierString;
    private String originalUser;

    // Broadcast receiver
    private ResponseReceiver receiver;

    // Progress display
    public ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);

        Intent initIntent = getIntent();
        identifierString = initIntent.getStringExtra(Constants.IDENTIFIER_STRING_KEY);
        originalUser = initIntent.getStringExtra(Constants.ORIGINAL_USER_KEY);

        // Filters for the receiver
        IntentFilter searchPeopleFilter = new IntentFilter(Constants.SEARCH_PEOPLE_RESP);
        searchPeopleFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, searchPeopleFilter);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Searching for people...");

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new FoundPeopleFragment()).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("----------");
        System.out.println("On Resume");
        System.out.println("----------");

        // Shows the progress dialog
        progress.show();

        // Starts the search people service
        Intent searchPeopleIntent = new Intent(this, SearchPeopleService.class);
        searchPeopleIntent.putExtra(Constants.IDENTIFIER_STRING_KEY, identifierString);
        startService(searchPeopleIntent);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    private void addListOnClickListener() {
        final ListView foundPeopleList = (ListView) findViewById(R.id.found_people_list);
        foundPeopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                UserRecord o = (UserRecord) foundPeopleList.getItemAtPosition(position);
                if (o != null) {
                    ArrayList<String> userData = JsonMethods.getUserData(o.userName);
                    String clickedUserName = userData.get(0);
                    if (!clickedUserName.equals(originalUser)) {
                        onListItemClick(userData);
                    }
                }
            }
        });
    }

    public void onListItemClick(ArrayList<String> userData) {
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

        // Starts the new activity
        startActivity(otherProfileIntent);
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("----------");
            System.out.println("On Receive");
            System.out.println("----------");

            // Gets the array list of user records from the broadcast intent
            ArrayList<UserRecord> people = intent.getParcelableArrayListExtra(Constants.PEOPLE_KEY);
            System.out.println("People: " + people);
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
        }
    }
}

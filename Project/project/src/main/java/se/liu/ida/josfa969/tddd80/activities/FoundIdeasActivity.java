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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.SearchIdeasService;
import se.liu.ida.josfa969.tddd80.fragments.FoundIdeasFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.item_records.IdeaRecord;
import se.liu.ida.josfa969.tddd80.list_adapters.IdeaItemAdapter;

public class FoundIdeasActivity extends Activity {
    // A tag of this class used by Log
    private final String ACTIVITY_TAG = "se.liu.ida.josfa969.tddd80.activities.FoundIdeasActivity";

    // Basic data
    private String tagString;
    private String originalUser;

    // An intent used to visit the detail view of an idea
    public Intent ideaDetailIntent;

    // Broadcast receiver
    private ResponseReceiver receiver;

    // Progress display
    public ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_ideas);
        Log.d(ACTIVITY_TAG, "On Create");

        Intent initIntent = getIntent();
        tagString = initIntent.getStringExtra(Constants.TAG_STRING_KEY);
        originalUser = initIntent.getStringExtra(Constants.ORIGINAL_USER_KEY);

        // Creates an intent used to visit the detail view of an idea
        ideaDetailIntent = new Intent(this, IdeaDetailActivity.class);

        // Creates the progress dialog
        progress = new ProgressDialog(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new FoundIdeasFragment()).commit();
        }
    }

    // Adds filters to the receiver
    private void addReceiverFilters() {
        Log.d(ACTIVITY_TAG, "Add Receiver Filters");
        // Filters for the receiver
        IntentFilter searchIdeasFilter = new IntentFilter(Constants.SEARCH_IDEAS_RESP);
        searchIdeasFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, searchIdeasFilter);
    }

    @Override
    protected void onResume() {
        addReceiverFilters();
        super.onResume();
        Log.d(ACTIVITY_TAG, "On Resume");

        // Shows the progress dialog
        progress.show();
        progress.setTitle("Loading");
        progress.setMessage("Searching for ideas...");

        // Starts the search ideas service
        Intent searchIdeasIntent = new Intent(this, SearchIdeasService.class);
        searchIdeasIntent.putExtra(Constants.TAG_STRING_KEY, tagString);
        searchIdeasIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
        startService(searchIdeasIntent);
    }

    @Override
    protected void onPause() {
        // Unregister the receiver
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            Log.e(ACTIVITY_TAG, "Receiver not registered", e);
            e.printStackTrace();
        }
        Log.d(ACTIVITY_TAG, "On Pause");
        super.onPause();
    }

    private void addListOnClickListener() {
        Log.d(ACTIVITY_TAG, "Add On List Click Listener");
        final ListView foundIdeasList = (ListView) findViewById(R.id.found_ideas_list);
        foundIdeasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                IdeaRecord ideaRecord = (IdeaRecord) foundIdeasList.getItemAtPosition(position);
                if (ideaRecord != null) {
                    ideaDetailIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
                    ideaDetailIntent.putExtra(Constants.POSTER_KEY, ideaRecord.poster);
                    ideaDetailIntent.putExtra(Constants.IDEA_TEXT_KEY, ideaRecord.ideaText);
                    ideaDetailIntent.putExtra(Constants.TAG_STRING_KEY, ideaRecord.tags);
                    ideaDetailIntent.putExtra(Constants.APPROVAL_NUM_KEY, ideaRecord.approvalNum);
                    ideaDetailIntent.putExtra(Constants.IDEA_ID_KEY, ideaRecord.ideaId);
                    startActivity(ideaDetailIntent);
                }
            }
        });
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(ACTIVITY_TAG, "On Receive");
            // Gets the array list of idea records from the broadcast intent
            ArrayList<IdeaRecord> ideas = intent.getParcelableArrayListExtra(Constants.IDEAS_KEY);
            TextView statusText = (TextView) findViewById(R.id.found_ideas_status_text);
            if (ideas == null) {
                Log.d(ACTIVITY_TAG, "No Ideas Found");
                statusText.setText("No Ideas Found");
            } else if (ideas.isEmpty()) {
                Log.d(ACTIVITY_TAG, "No Ideas Found");
                statusText.setText("No Ideas Found");
            } else {
                ListView foundIdeasList = (ListView) findViewById(R.id.found_ideas_list);
                foundIdeasList.setAdapter(new IdeaItemAdapter(context, R.layout.idea_list_item, ideas, originalUser));
            }

            // Dismisses the progress dialog
            progress.dismiss();
            addListOnClickListener();
        }
    }
}

package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.SearchIdeasService;
import se.liu.ida.josfa969.tddd80.fragments.FoundIdeasFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.IdeaItemAdapter;
import se.liu.ida.josfa969.tddd80.help_classes.IdeaRecord;

public class FoundIdeasActivity extends Activity {
    private String tagString;

    // Broadcast receiver
    private ResponseReceiver receiver;

    // Progress display
    public ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_ideas);

        Intent initIntent = getIntent();
        tagString = initIntent.getStringExtra(Constants.TAG_STRING_KEY);

        // Filters for the receiver
        IntentFilter searchIdeasFilter = new IntentFilter(Constants.SEARCH_IDEAS_RESP);
        searchIdeasFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, searchIdeasFilter);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Searching for ideas...");

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new FoundIdeasFragment()).commit();
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

        // Starts the search ideas service
        Intent searchIdeasIntent = new Intent(this, SearchIdeasService.class);
        searchIdeasIntent.putExtra(Constants.TAG_STRING_KEY, tagString);
        startService(searchIdeasIntent);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    public class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("----------");
            System.out.println("On Receive");
            System.out.println("----------");

            // Gets the array list of idea records from the broadcast intent
            ArrayList<IdeaRecord> ideas = intent.getParcelableArrayListExtra(Constants.IDEAS_KEY);
            System.out.println("Ideas: " + ideas);
            if (ideas == null) {
                TextView statusText = (TextView) findViewById(R.id.found_ideas_status_text);
                statusText.setText("No Ideas Found");
            } else if (ideas.isEmpty()) {
                TextView statusText = (TextView) findViewById(R.id.found_ideas_status_text);
                statusText.setText("No Ideas Found");
            } else {
                ListView foundIdeasList = (ListView) findViewById(R.id.found_ideas_list);
                foundIdeasList.setAdapter(new IdeaItemAdapter(context, R.layout.idea_list_item, ideas));
            }

            // Dismisses the progress dialog
            progress.dismiss();
        }
    }
}

package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.SearchIdeasService;
import se.liu.ida.josfa969.tddd80.fragments.FoundIdeasFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.UserRecord;
import se.liu.ida.josfa969.tddd80.list_adapters.IdeaItemAdapter;
import se.liu.ida.josfa969.tddd80.item_records.IdeaRecord;

public class FoundIdeasActivity extends Activity {
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

        Intent initIntent = getIntent();
        tagString = initIntent.getStringExtra(Constants.TAG_STRING_KEY);
        originalUser = initIntent.getStringExtra(Constants.ORIGINAL_USER_KEY);

        // Creates an intent used to get basic user data
        ideaDetailIntent = new Intent(this, IdeaDetailActivity.class);

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
        searchIdeasIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
        startService(searchIdeasIntent);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    private void addListOnClickListener() {
        final ListView foundIdeasList = (ListView) findViewById(R.id.found_ideas_list);
        foundIdeasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                IdeaRecord o = (IdeaRecord) foundIdeasList.getItemAtPosition(position);
                if (o != null) {
                    ideaDetailIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
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

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Gets the array list of idea records from the broadcast intent
            ArrayList<IdeaRecord> ideas = intent.getParcelableArrayListExtra(Constants.IDEAS_KEY);
            TextView statusText = (TextView) findViewById(R.id.found_ideas_status_text);
            if (ideas == null) {
                statusText.setText("No Ideas Found");
            } else if (ideas.isEmpty()) {
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

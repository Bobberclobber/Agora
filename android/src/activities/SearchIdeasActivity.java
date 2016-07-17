package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.fragments.SearchIdeasFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;

public class SearchIdeasActivity extends Activity {
    // A tag of this class used by Log
    private final String ACTIVITY_TAG = "se.liu.ida.josfa969.tddd80.activities.SearchIdeasActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ideas);
        Log.d(ACTIVITY_TAG, "On Create");

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new SearchIdeasFragment()).commit();
        }
    }

    public void onIdeaSearchClick(View view) {
        Log.d(ACTIVITY_TAG, "On Idea Search Click");
        // Gets input
        EditText tagInput = (EditText) findViewById(R.id.search_tags_input);
        String tagString = String.valueOf(tagInput.getText());

        if (tagString.equals("")) {
            Log.e(ACTIVITY_TAG, "No tags entered");
            ((TextView) findViewById(R.id.search_ideas_status_text)).setText("You have to enter at least one tag");
        } else {
            Intent ideasSearchIntent = new Intent(this, FoundIdeasActivity.class);
            ideasSearchIntent.putExtra(Constants.TAG_STRING_KEY, tagString);
            ideasSearchIntent.putExtra(Constants.ORIGINAL_USER_KEY, getIntent().getStringExtra(Constants.ORIGINAL_USER_KEY));
            startActivity(ideasSearchIntent);
        }
    }
}

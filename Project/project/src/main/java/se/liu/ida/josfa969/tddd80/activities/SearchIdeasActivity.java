package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.fragments.SearchIdeasFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;

public class SearchIdeasActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ideas);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new SearchIdeasFragment()).commit();
        }
    }

    public void onIdeaSearchClick(View view) {
        // Gets input
        EditText tagInput = (EditText) findViewById(R.id.search_tags_input);
        String tagString = String.valueOf(tagInput.getText());

        if (tagString.equals("")) {
            ((TextView) findViewById(R.id.search_ideas_status_text)).setText("You have to enter at least one tag");
        } else {
            Intent ideasSearchIntent = new Intent(this, FoundIdeasActivity.class);
            ideasSearchIntent.putExtra(Constants.TAG_STRING_KEY, tagString);
            startActivity(ideasSearchIntent);
        }
    }
}

package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.fragments.SearchPeopleFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;

public class SearchPeopleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_people);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new SearchPeopleFragment()).commit();
        }
    }

    public void onPeopleSearchClick(View view) {
        // Gets input
        EditText identifierInput = (EditText) findViewById(R.id.search_people_input);
        String identifierString = String.valueOf(identifierInput.getText());

        if (identifierString.equals("")) {
            ((TextView) findViewById(R.id.search_people_status_text)).setText("You have to enter a search query");
        } else {
            Intent peopleSearchIntent = new Intent(this, FoundPeopleActivity.class);
            peopleSearchIntent.putExtra(Constants.IDENTIFIER_STRING_KEY, identifierString);
            peopleSearchIntent.putExtra(Constants.ORIGINAL_USER_KEY, getIntent().getStringExtra(Constants.ORIGINAL_USER_KEY));
            startActivity(peopleSearchIntent);
        }
    }
}

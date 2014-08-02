package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.fragments.SearchPeopleFragment;

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
        Intent peopleSearchIntent = new Intent(this, FoundPeopleActivity.class);
        startActivity(peopleSearchIntent);
    }
}

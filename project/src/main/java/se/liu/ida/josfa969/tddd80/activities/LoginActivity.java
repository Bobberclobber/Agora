package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.fragments.LoginFragment;

public class LoginActivity extends Activity {

    private String temp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Gets the intent which started this activity
        Intent initIntent = getIntent();
        // Extracts the data sent with the intent based on the key
        // name given to it when it was bundled with the intent
        temp = initIntent.getStringExtra(InitialActivity.EXTRA_TEST_MSG);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new LoginFragment()).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Changes one of the TextView's content to the string passed with the intent
        ((TextView) findViewById(R.id.temp_tv)).setText(temp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onLoginClick(View view) {
        Intent loginIntent = new Intent(this, ProfileActivity.class);
        startActivity(loginIntent);
    }
}

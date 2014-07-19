package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.fragments.InitialFragment;

public class InitialActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new InitialFragment()).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public void onRegisterClick(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    // Data used for a test of sending data along with an intent
    String testMsg = "Test Msg";
    public static String EXTRA_TEST_MSG = "se.liu.ida.josfa969.tdd80.activities.TEST_MSG";

    public void onLoginClick(View view) {
        // Creates an intent to switch activity
        Intent loginIntent = new Intent(this, LoginActivity.class);
        // Bundles test data with the intent
        loginIntent.putExtra(EXTRA_TEST_MSG, testMsg);
        startActivity(loginIntent);
    }

    public void onPreviewClick(View view) {
    }
}

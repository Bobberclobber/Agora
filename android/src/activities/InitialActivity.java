package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.fragments.InitialFragment;

public class InitialActivity extends Activity {
    // A tag of this class used by Log
    private final String ACTIVITY_TAG = "se.liu.ida.josfa969.tddd80.activities.InitialActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Log.d(ACTIVITY_TAG, "On Create");

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new InitialFragment()).commit();
        }
    }

    public void onRegisterClick(View view) {
        Log.d(ACTIVITY_TAG, "On Register Click");
        // Starts the register user activity
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    public void onLoginClick(View view) {
        Log.d(ACTIVITY_TAG, "On Login Click");
        // Creates an intent to switch activity
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    public void onPreviewClick(View view) {
        Log.d(ACTIVITY_TAG, "On Preview Click");
    }
}

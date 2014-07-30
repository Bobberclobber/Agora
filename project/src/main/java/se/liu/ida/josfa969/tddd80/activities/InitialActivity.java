package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import se.liu.ida.josfa969.tddd80.JsonMethods;
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

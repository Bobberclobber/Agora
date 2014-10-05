package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.GetUserDataService;
import se.liu.ida.josfa969.tddd80.background_services.LoginUserService;
import se.liu.ida.josfa969.tddd80.fragments.LoginFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;

public class LoginActivity extends Activity {
    // A tag of this class used by Log
    private final String ACTIVITY_TAG = "se.liu.ida.josfa969.tddd80.activities.LoginActivity";

    // Basic data
    String identifier;
    String password;

    // An intent used to login the user
    Intent loginIntent;

    // An intent used to start the LoginUser service
    Intent loginUserIntent;

    // An intent used to start the GetUserData service
    Intent getUserDataIntent;

    // Broadcast receiver
    ResponseReceiver receiver;

    // The status text view
    TextView statusText;

    // Progress dialog
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(ACTIVITY_TAG, "On Create");

        // Creates the login intent
        loginIntent = new Intent(this, ProfileActivity.class);

        // Creates the login user intent
        loginUserIntent = new Intent(this, LoginUserService.class);

        // Creates the get user data intent
        getUserDataIntent = new Intent(this, GetUserDataService.class);

        // Creates the progress dialog
        progress = new ProgressDialog(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new LoginFragment()).commit();
        }
    }

    // Adds filters to the receiver
    private void addReceiverFilters() {
        Log.d(ACTIVITY_TAG, "Add Receiver Filters");
        // Filters for the receiver
        IntentFilter loginUserFilter = new IntentFilter(Constants.LOGIN_USER_RESP);
        IntentFilter getUserDataFilter = new IntentFilter(Constants.GET_USER_DATA_RESP);
        loginUserFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getUserDataFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, loginUserFilter);
        registerReceiver(receiver, getUserDataFilter);
    }

    @Override
    protected void onResume() {
        addReceiverFilters();
        super.onResume();
        Log.d(ACTIVITY_TAG, "On Resume");
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            Log.e(ACTIVITY_TAG, "Receiver not registered", e);
            e.printStackTrace();
        }
        Log.d(ACTIVITY_TAG, "On Pause");
        super.onPause();
    }

    public void onLoginClick(View view) {
        Log.d(ACTIVITY_TAG, "On Login Click");
        // Get edit text views
        EditText identifierInput = (EditText) findViewById(R.id.login_identifier_input);
        EditText passwordInput = (EditText) findViewById(R.id.login_password_input);

        // Get the status text view
        statusText = (TextView) findViewById(R.id.login_status_text);

        // Get the view's input
        identifier = String.valueOf(identifierInput.getText());
        password = String.valueOf(passwordInput.getText());

        if (identifier.equals("") && password.equals("")) {
            Log.e(ACTIVITY_TAG, "Inputs are empty");
            statusText.setText("Inputs are empty");
        } else if (identifier.equals("")) {
            Log.e(ACTIVITY_TAG, "Identifier empty");
            statusText.setText("Enter username or e-mail");
        } else if (password.equals("")) {
            Log.e(ACTIVITY_TAG, "Password empty");
            statusText.setText("Enter your password");
        } else {
            // Show the loading spinner
            progress.setTitle("Loading");
            progress.setMessage("Wait While Login In...");
            progress.show();
            // Starts the login user service
            loginUserIntent.putExtra(Constants.IDENTIFIER_STRING_KEY, identifier);
            loginUserIntent.putExtra(Constants.PASSWORD_KEY, password);
            startService(loginUserIntent);
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(ACTIVITY_TAG, "On Receive");
            String intentAction = intent.getAction();
            if (intentAction != null) {
                if (intentAction.equals(Constants.LOGIN_USER_RESP)) {
                    Log.d(ACTIVITY_TAG, "LOGIN_USER_RESP");
                    String response = intent.getStringExtra(Constants.RESPONSE_KEY);
                    if (response != null) {
                        if (response.equals("Success")) {
                            String userName = intent.getStringExtra(Constants.USER_NAME_KEY);
                            getUserDataIntent.putExtra(Constants.USER_NAME_KEY, userName);
                            startService(getUserDataIntent);
                        } else {
                            Log.e(ACTIVITY_TAG, response);
                            progress.dismiss();
                            statusText.setText(response);
                        }
                    }
                } else if (intentAction.equals(Constants.GET_USER_DATA_RESP)) {
                    Log.d(ACTIVITY_TAG, "GET_USER_DATA_RESP");
                    // Dismisses the progress dialog
                    progress.dismiss();
                    // Gets the user's basic data
                    ArrayList<String> userData = intent.getStringArrayListExtra(Constants.USER_DATA_KEY);
                    if (userData != null) {
                        // Sends the user data extras to the next activity
                        loginIntent.putExtra(Constants.USER_NAME_KEY, userData.get(0));
                        loginIntent.putExtra(Constants.E_MAIL_KEY, userData.get(1));
                        loginIntent.putExtra(Constants.PASSWORD_KEY, password);
                        loginIntent.putExtra(Constants.COUNTRY_KEY, userData.get(2));
                        loginIntent.putExtra(Constants.CITY_KEY, userData.get(3));
                        loginIntent.putExtra(Constants.LOCATION_KEY, userData.get(5));
                        startActivity(loginIntent);
                    }
                }
            }
        }
    }
}

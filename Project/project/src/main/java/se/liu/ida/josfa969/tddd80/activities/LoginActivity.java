package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.fragments.LoginFragment;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new LoginFragment()).commit();
        }
    }

    public void onLoginClick(View view) {
        // Get edit text views
        EditText identifierInput = (EditText) findViewById(R.id.login_identifier_input);
        EditText passwordInput = (EditText) findViewById(R.id.login_password_input);

        // Get the status text view
        TextView statusText = (TextView) findViewById(R.id.login_status_text);

        // Get the view's input
        String identifier = String.valueOf(identifierInput.getText());
        String password = String.valueOf(passwordInput.getText());

        if (identifier.equals("") && password.equals("")){
            statusText.setText("Inputs are empty");
        } else if (identifier.equals("")) {
            statusText.setText("Enter username or e-mail");
        } else if (password.equals("")) {
            statusText.setText("Enter your password");
        } else {
            // Create and show the loading spinner
            ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait While Login In...");
            progress.show();

            // Gets the JSON response from the given input
            String response = JsonMethods.loginUser(identifier, password);

            if (response.equals("Success")) {
                progress.dismiss();
                // Create an intent to start Profile Activity
                Intent loginIntent = new Intent(this, ProfileActivity.class);
                // Gets the user's basic data
                ArrayList<String> userData = JsonMethods.getUserData(identifier);
                // Send the identifier as an extra to the next activity
                if (identifier.contains("@")) {
                    loginIntent.putExtra(Constants.USER_NAME_KEY, JsonMethods.getUserName(identifier));
                    loginIntent.putExtra(Constants.E_MAIL_KEY, identifier);
                } else {
                    loginIntent.putExtra(Constants.USER_NAME_KEY, identifier);
                    loginIntent.putExtra(Constants.E_MAIL_KEY, JsonMethods.getEMail(identifier));
                }
                loginIntent.putExtra(Constants.PASSWORD_KEY, password);
                loginIntent.putExtra(Constants.COUNTRY_KEY, userData.get(2));
                loginIntent.putExtra(Constants.CITY_KEY, userData.get(3));
                startActivity(loginIntent);
            } else {
                progress.dismiss();
                statusText.setText(response);
            }
        }
    }
}

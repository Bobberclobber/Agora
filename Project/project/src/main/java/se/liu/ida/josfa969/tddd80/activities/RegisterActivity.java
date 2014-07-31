package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.fragments.RegisterFragment;

public class RegisterActivity extends Activity {

    private String userNameKey = Constants.USER_NAME_KEY;
    private String eMailKey = Constants.E_MAIL_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_fragment);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new RegisterFragment()).commit();
        }
    }

    public void onCompleteClick(View view) {
        // Get edit text views
        EditText userNameInput = (EditText) findViewById(R.id.user_name_input_field);
        EditText passwordInput = (EditText) findViewById(R.id.password_input_field);
        EditText confirmInput = (EditText) findViewById(R.id.confirm_password_input_field);
        EditText eMailInput = (EditText) findViewById(R.id.email_input_field);
        EditText countryInput = (EditText) findViewById(R.id.country_input_field);
        EditText cityInput = (EditText) findViewById(R.id.city_input_field);

        // Get the status text view
        TextView statusText = (TextView) findViewById(R.id.register_status_text);

        // Get the view's input
        String userName = String.valueOf(userNameInput.getText());
        String password = String.valueOf(passwordInput.getText());
        String confirm = String.valueOf(confirmInput.getText());
        String eMail = String.valueOf(eMailInput.getText());
        String country = String.valueOf(countryInput.getText());
        String city = String.valueOf(cityInput.getText());

        if (fieldIsEmpty(userName, password, eMail, country, city)) {
            statusText.setText("You have to fill out all fields");
        } else if (userName.contains("@")) {
            statusText.setText("The user name may not contain '@'");
        } else if (!password.equals(confirm)) {
            statusText.setText("The passwords don't match");
        } else {
            // Create and show the loading spinner
            ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait While Registering...");
            progress.show();

            // Gets the JSON response from the given input
            String response = JsonMethods.registerNewUser(userName, password, eMail, country, city);

            if (response.equals("Success")) {
                progress.dismiss();
                // Create an intent to start the Profile Activity
                Intent completeIntent = new Intent(this, ProfileActivity.class);
                completeIntent.putExtra(userNameKey, userName);
                completeIntent.putExtra(eMailKey, eMail);
                startActivity(completeIntent);
            } else {
                progress.dismiss();
                statusText.setText(response);
            }
        }
    }

    private boolean fieldIsEmpty(String userName, String password, String eMail, String country, String city) {
        if (userName.equals("")) {
            return true;
        } else if (password.equals("")) {
            return true;
        } else if (eMail.equals("")) {
            return true;
        } else if (country.equals("")) {
            return true;
        } else if (city.equals("")) {
            return true;
        }
        return false;
    }
}

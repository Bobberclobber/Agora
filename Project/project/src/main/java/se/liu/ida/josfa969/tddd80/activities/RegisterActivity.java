package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.RegisterUserService;
import se.liu.ida.josfa969.tddd80.fragments.RegisterFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;

public class RegisterActivity extends Activity {
    // Data entered by the user
    private String userName;
    private String eMail;
    private String password;
    private String country;
    private String city;
    private String image;

    // The status text view
    TextView statusText;

    // Broadcast receiver
    ResponseReceiver receiver;

    // Progress dialog
    ProgressDialog progress;

    // Intent to complete the registration
    Intent completeIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_fragment);

        // Adds filters for the receiver
        IntentFilter registerUserFilter = new IntentFilter(Constants.REGISTER_USER_RESP);
        registerUserFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, registerUserFilter);

        // Creates the progress dialog
        progress = new ProgressDialog(this);

        // Creates the complete intent
        completeIntent = new Intent(this, ProfileActivity.class);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new RegisterFragment()).commit();
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusText = (TextView) findViewById(R.id.register_status_text);
    }

    public void onCompleteClick(View view) {
        // Get edit text views
        EditText userNameInput = (EditText) findViewById(R.id.user_name_input_field);
        EditText passwordInput = (EditText) findViewById(R.id.password_input_field);
        EditText confirmInput = (EditText) findViewById(R.id.confirm_password_input_field);
        EditText eMailInput = (EditText) findViewById(R.id.email_input_field);
        EditText countryInput = (EditText) findViewById(R.id.country_input_field);
        EditText cityInput = (EditText) findViewById(R.id.city_input_field);

        // Get the view's input
        userName = String.valueOf(userNameInput.getText());
        password = String.valueOf(passwordInput.getText());
        String confirm = String.valueOf(confirmInput.getText());
        eMail = String.valueOf(eMailInput.getText());
        country = String.valueOf(countryInput.getText());
        city = String.valueOf(cityInput.getText());

        if (fieldIsEmpty(userName, password, eMail, country, city)) {
            statusText.setText("You have to fill out all fields");
        } else if (userName.contains("@")) {
            statusText.setText("The user name may not contain '@'");
        } else if (!password.equals(confirm)) {
            statusText.setText("The passwords don't match");
        } else {
            // Create and show the loading spinner
            progress.setTitle("Loading");
            progress.setMessage("Wait While Registering...");
            progress.show();

            // Gets the default avatar image
            Bitmap avatarImg = BitmapFactory.decodeResource(this.getResources(), R.drawable.avatar_img);
            // Turns the bitmap avatar image to a string which can be passed with a URL
            image = Constants.bitmapToString(avatarImg);

            Intent registerUserIntent = new Intent(this, RegisterUserService.class);
            registerUserIntent.putExtra(Constants.USER_NAME_KEY, userName);
            registerUserIntent.putExtra(Constants.PASSWORD_KEY, password);
            registerUserIntent.putExtra(Constants.E_MAIL_KEY, eMail);
            registerUserIntent.putExtra(Constants.COUNTRY_KEY, country);
            registerUserIntent.putExtra(Constants.CITY_KEY, city);
            registerUserIntent.putExtra(Constants.AVATAR_IMAGE_KEY, image);
            startService(registerUserIntent);
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

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra(Constants.RESPONSE_KEY);
            if (response != null) {
                if (response.equals("Success")) {
                    progress.dismiss();
                    // Create an intent to start the Profile Activity
                    completeIntent.putExtra(Constants.USER_NAME_KEY, userName);
                    completeIntent.putExtra(Constants.E_MAIL_KEY, eMail);
                    completeIntent.putExtra(Constants.PASSWORD_KEY, password);
                    completeIntent.putExtra(Constants.COUNTRY_KEY, country);
                    completeIntent.putExtra(Constants.CITY_KEY, city);
                    completeIntent.putExtra(Constants.LOCATION_KEY, "Not set");
                    completeIntent.putExtra(Constants.AVATAR_IMAGE_KEY, image);
                    startActivity(completeIntent);
                } else {
                    progress.dismiss();
                    statusText.setText(response);
                }
            }
        }
    }
}

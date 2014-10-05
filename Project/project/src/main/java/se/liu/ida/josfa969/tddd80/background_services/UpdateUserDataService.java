package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * Created by Josef on 04/08/14.
 *
 * A service called when the user clicks the
 * "Update" button on the profile settings
 * page
 */
public class UpdateUserDataService extends IntentService {

    public UpdateUserDataService() {
        super("UpdateUserDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Gets data
        String originalUserName = intent.getStringExtra(Constants.ORIGINAL_USER_KEY);
        String originalEMail = intent.getStringExtra(Constants.ORIGINAL_E_MAIL_KEY);
        String newUserName = intent.getStringExtra(Constants.USER_NAME_KEY);
        String newEMail = intent.getStringExtra(Constants.E_MAIL_KEY);
        String newPassword = intent.getStringExtra(Constants.PASSWORD_KEY);
        String newCountry = intent.getStringExtra(Constants.COUNTRY_KEY);
        String newCity = intent.getStringExtra(Constants.CITY_KEY);
        String newLocation = intent.getStringExtra(Constants.LOCATION_KEY);

        String result = JsonMethods.updateUserData(originalUserName, originalEMail, newUserName, newEMail, newPassword,
                                                   newCountry, newCity, newLocation);
        Log.d("UpdateUserDataService", "Result: " + result);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.UPDATE_USER_DATA_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.USER_DATA_UPDATE_MSG_KEY, result);
        sendBroadcast(broadCastIntent);
    }
}

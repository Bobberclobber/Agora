package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * Created by Josef on 17/08/14.
 *
 * A service used to register a user
 */
public class RegisterUserService extends IntentService {
    public RegisterUserService() {
        super("RegisterUserService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userName = intent.getStringExtra(Constants.USER_NAME_KEY);
        String password = intent.getStringExtra(Constants.PASSWORD_KEY);
        String eMail = intent.getStringExtra(Constants.E_MAIL_KEY);
        String country = intent.getStringExtra(Constants.COUNTRY_KEY);
        String city = intent.getStringExtra(Constants.CITY_KEY);
        String response = JsonMethods.registerNewUser(userName, password, eMail, country, city);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.REGISTER_USER_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.RESPONSE_KEY, response);
        sendBroadcast(broadCastIntent);
    }
}

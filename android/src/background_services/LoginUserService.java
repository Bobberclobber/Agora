package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;

/**
 * Created by Josef on 15/08/14.
 *
 * A service used to login a user
 */
public class LoginUserService extends IntentService {
    public LoginUserService() {
        super("LoginUserService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String identifier = intent.getStringExtra(Constants.IDENTIFIER_STRING_KEY);
        String password = intent.getStringExtra(Constants.PASSWORD_KEY);
        String response = JsonMethods.loginUser(identifier, password);
        String userName = identifier;
        if (userName != null) {
            if (userName.contains("@")) {
                userName = JsonMethods.getUserName(identifier);
            }
        }

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.LOGIN_USER_RESP);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putExtra(Constants.RESPONSE_KEY, response);
        broadCastIntent.putExtra(Constants.USER_NAME_KEY, userName);
        sendBroadcast(broadCastIntent);
    }
}

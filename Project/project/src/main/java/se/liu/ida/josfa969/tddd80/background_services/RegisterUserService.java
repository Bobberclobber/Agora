package se.liu.ida.josfa969.tddd80.background_services;

import android.app.IntentService;
import android.content.Intent;

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

    }
}

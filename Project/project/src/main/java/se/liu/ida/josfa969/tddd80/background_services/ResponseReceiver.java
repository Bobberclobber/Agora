package se.liu.ida.josfa969.tddd80.background_services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.IdeaRecord;

/**
 * Created by Josef on 30/07/14.
 *
 * A broadcast receiver
 */
public class ResponseReceiver extends BroadcastReceiver {

    // Prevents instantiation
    private ResponseReceiver() {
    }

    // Called
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("----------");
        System.out.println("On Receive");
        System.out.println("----------");

        ArrayList<IdeaRecord> ideas = intent.getParcelableArrayListExtra(Constants.BROADCAST_IDEAS_KEY);
    }
}

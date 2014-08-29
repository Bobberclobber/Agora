package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.GetConversationService;
import se.liu.ida.josfa969.tddd80.background_services.SendMessageService;
import se.liu.ida.josfa969.tddd80.fragments.ConversationFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.MessageRecord;
import se.liu.ida.josfa969.tddd80.list_adapters.MessageItemAdapter;

/**
 * An activity displaying a conversation between two users.
 * The following data should be attached
 * to the intent starting this activity:
 * <br/>
 * USER_NAME_KEY - The user name of the user not using the current instance of the application
 * ORIGINAL_USER_KEY - The user name of the user currently using the application
 */
public class ConversationActivity extends Activity {
    // A tag of this class used by Log
    private final String ACTIVITY_TAG = "se.liu.ida.josfa969.tddd80.activities.ConversationActivity";

    // Initializes basic data variables
    private String userName = null;
    private String avatarImage = "";
    private String originalUser = null;

    // Broadcast receiver
    private ResponseReceiver receiver;

    // Intent to start the get recent messages service
    public Intent getConversationIntent;

    // Progress dialog
    public ProgressDialog progress;

    // Objects used when checking for new messages
    Handler handler;
    Timer timer;
    UpdateMessagesTask updateMessagesTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Requests to use a feature which displays a progress bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Log.d(ACTIVITY_TAG, "On Create");

        // Gets all data sent by the intent starting this activity
        Intent initIntent = getIntent();
        userName = initIntent.getStringExtra(Constants.USER_NAME_KEY);
        avatarImage = initIntent.getStringExtra(Constants.AVATAR_IMAGE_KEY);
        originalUser = initIntent.getStringExtra(Constants.ORIGINAL_USER_KEY);

        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        String defaultUserName = "User Name";
        String defaultOriginalUser = "You";

        if (userName == null) {
            userName = preferences.getString(Constants.USER_NAME_KEY, defaultUserName);
        }
        if (originalUser == null) {
            originalUser = preferences.getString(Constants.ORIGINAL_USER_KEY, defaultOriginalUser);
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new ConversationFragment()).commit();
        }

        getConversationIntent = new Intent(getBaseContext(), GetConversationService.class);
        progress = new ProgressDialog(this);
    }

    // Adds filters to the receiver
    private void addReceiverFilters() {
        Log.d(ACTIVITY_TAG, "Add Receiver Filters");
        // Filters for the receiver
        IntentFilter getRecentMessagesFilter = new IntentFilter(Constants.GET_CONVERSATION_RESP);
        IntentFilter sendMessageFilter = new IntentFilter(Constants.SEND_MESSAGE_RESP);
        getRecentMessagesFilter.addCategory(Intent.CATEGORY_DEFAULT);
        sendMessageFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, getRecentMessagesFilter);
        registerReceiver(receiver, sendMessageFilter);
    }

    @Override
    protected void onPause() {
        // Stops the automatic updating of messages
        updateMessagesTask.cancel();

        // Unregister the receiver
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            Log.e(ACTIVITY_TAG, "Receiver not registered", e);
            e.printStackTrace();
        }

        Log.d(ACTIVITY_TAG, "On Pause");
        super.onPause();

        // When leaving this activity and starting a
        // new one, save the current user's username
        // and e-mail using a shared preference
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.USER_NAME_KEY, userName);
        editor.putString(Constants.ORIGINAL_USER_KEY, originalUser);
        editor.commit();
    }

    @Override
    protected void onResume() {
        addReceiverFilters();
        super.onResume();
        Log.d(ACTIVITY_TAG, "On Pause");

        TextView conversationPartnerName = (TextView) findViewById(R.id.conversation_partner_name);
        ImageButton avatarImageButton = (ImageButton) findViewById(R.id.avatar_picture);
        conversationPartnerName.setText(userName);
        avatarImageButton.setImageBitmap(Constants.stringToBitmap(avatarImage));

        handler = new Handler();
        timer = new Timer();
        updateMessagesTask = new UpdateMessagesTask(handler);
        timer.schedule(updateMessagesTask, 0, 10000);
    }

    // A timer task which every 10 seconds checks
    // if any new messages has been sent
    private class UpdateMessagesTask extends TimerTask {
        private Handler handler;

        private UpdateMessagesTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // Displays a progress bar
                    setProgressBarIndeterminateVisibility(true);
                    getConversationIntent.putExtra(Constants.USER_NAME_KEY, userName);
                    getConversationIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
                    startService(getConversationIntent);
                }
            });
        }
    }

    public void onProfileImageClick(View view) {
        Log.d(ACTIVITY_TAG, "On Profile Image Click");
        // Gets the user's data
        ArrayList<String> userData = JsonMethods.getUserData(userName);
        String eMail = userData.get(1);
        String country = userData.get(2);
        String city = userData.get(3);
        String followers = userData.get(4);
        String location = userData.get(5);

        Intent otherProfileIntent = new Intent(this, OtherProfileActivity.class);

        // Attaches the basic data to the intent
        otherProfileIntent.putExtra(Constants.USER_NAME_KEY, userName);
        otherProfileIntent.putExtra(Constants.E_MAIL_KEY, eMail);
        otherProfileIntent.putExtra(Constants.COUNTRY_KEY, country);
        otherProfileIntent.putExtra(Constants.CITY_KEY, city);
        otherProfileIntent.putExtra(Constants.FOLLOWERS_KEY, followers);
        otherProfileIntent.putExtra(Constants.LOCATION_KEY, location);
        otherProfileIntent.putExtra(Constants.AVATAR_IMAGE_KEY, avatarImage);
        otherProfileIntent.putExtra(Constants.ORIGINAL_USER_KEY, userName);

        // Starts the new activity
        startActivity(otherProfileIntent);
    }

    public void onSendMessageClick(View view) {
        Log.d(ACTIVITY_TAG, "On Send Message Click");
        progress.setTitle("Loading");
        progress.setMessage("Sending message...");
        progress.show();

        // Gets the message text
        EditText messageInput = (EditText) findViewById(R.id.send_message_input);
        String messageText = String.valueOf(messageInput.getText());
        if (messageText.equals("")) {
            progress.dismiss();
            String toastMsg = "You have to write something.";
            Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
        } else {
            messageInput.setText("");
            Intent sendMessageIntent = new Intent(this, SendMessageService.class);
            sendMessageIntent.putExtra(Constants.SENDER_KEY, originalUser);
            sendMessageIntent.putExtra(Constants.RECEIVER_KEY, userName);
            sendMessageIntent.putExtra(Constants.MESSAGE_TEXT_KEY, messageText);
            startService(sendMessageIntent);
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(ACTIVITY_TAG, "On Receive");
            String intentAction = intent.getAction();
            if (intentAction != null) {
                if (intent.getAction().equals(Constants.SEND_MESSAGE_RESP)) {
                    Log.d(ACTIVITY_TAG, "SEND_MESSAGE_RESP");
                    progress.dismiss();
                    getConversationIntent.putExtra(Constants.USER_NAME_KEY, userName);
                    getConversationIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
                    startService(getConversationIntent);
                } else if (intent.getAction().equals(Constants.GET_CONVERSATION_RESP)) {
                    Log.d(ACTIVITY_TAG, "GET_CONVERSATION_RESP");
                    ListView messagesList = (ListView) findViewById(R.id.messages_list);
                    ArrayList<MessageRecord> messages = intent.getParcelableArrayListExtra(Constants.MESSAGES_KEY);
                    messagesList.setAdapter(new MessageItemAdapter(context, R.layout.message_list_item, messages));
                    if (messages != null) {
                        messagesList.setSelection(messages.size());
                    }
                    // Hides the progress bar
                    setProgressBarIndeterminateVisibility(false);
                }
            }
        }
    }
}

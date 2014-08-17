package se.liu.ida.josfa969.tddd80.activities;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.GetIdeaFeedService;
import se.liu.ida.josfa969.tddd80.background_services.GetMessageFeedService;
import se.liu.ida.josfa969.tddd80.background_services.PostIdeaService;
import se.liu.ida.josfa969.tddd80.background_services.UpdateUserDataService;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.ProfilePagerAdapter;
import se.liu.ida.josfa969.tddd80.item_records.IdeaRecord;
import se.liu.ida.josfa969.tddd80.item_records.MessageRecord;
import se.liu.ida.josfa969.tddd80.list_adapters.IdeaItemAdapter;
import se.liu.ida.josfa969.tddd80.list_adapters.MessageItemAdapter;

public class ProfileActivity extends FragmentActivity {
    // Variables used for the photo capture feature
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    // Variables used for the set home location feature
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // When requested, this adapter returns one of the
    // following different Fragments: IdeaFeedFragment,
    // PostIdeaFragment, FindFragment or ProfileSettingsFragment
    // depending on which number is swiped to
    ProfilePagerAdapter pagerAdapter;
    ViewPager profilePager;
    ViewPager.SimpleOnPageChangeListener onPageChangeListener;

    // The user data used when fetching data
    private String userName = null;
    private String eMail = null;
    private String password = null;
    private String country = null;
    private String city = null;

    // The user data from the updated user settings
    private String newUserName;
    private String newEMail;
    private String newPassword;
    private String newCountry;
    private String newCity;

    // A list of tab titles
    private String[] tabTitleList = {"Post", "Idea Feed", "Messages", "Find", "Settings"};

    // An intent used to visit the detail view of an idea
    public Intent ideaDetailIntent;

    // An intent used to view a conversation between to users
    public Intent conversationIntent;

    // Broadcast receiver
    private ResponseReceiver receiver;

    // Progress dialog
    public ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Requests to use a feature which displays a progress bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Gets all the user data defined by variables above
        getUserData();

        // Add tabs to the action bar
        final ActionBar actionBar = getActionBar();

        // Creates the progress dialog
        progress = new ProgressDialog(this);

        // Creates the page change listener
        createPageChangeListener();

        // ViewPager and its adapter use support library fragments,
        // so use getSupportFragmentManager
        pagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager());
        profilePager = ((ViewPager) findViewById(R.id.profile_pager));
        profilePager.setAdapter(pagerAdapter);
        profilePager.setOnPageChangeListener(onPageChangeListener);

        // Specify that tabs should be displayed in the action bar
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }

        // Create a tab listener that is called when the user changes tabs
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                // When the tab is selected, switch to the corresponding tab in the ViewPager
                profilePager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }
        };

        // Add the tabs
        if (actionBar != null) {
            for (String tabTitle : tabTitleList) {
                ActionBar.Tab tempTab = actionBar.newTab();
                tempTab.setText(tabTitle);
                tempTab.setTabListener(tabListener);
                actionBar.addTab(tempTab);
            }
        }

        // Adds filters to the receiver
        addReceiverFilters();
    }

    // Creates the listener for when the page changes
    private void createPageChangeListener() {
        onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // When swiping between pages, select the corresponding tab
                ActionBar ab = getActionBar();
                if (ab != null) {
                    ab.setSelectedNavigationItem(position);
                }
                if (position == 1) {
                    // Displays a progress bar
                    setProgressBarIndeterminateVisibility(true);
                    // Starts the get idea feed service
                    startIdeaFeedService();
                } else if (position == 2) {
                    // Displays a progress bar
                    setProgressBarIndeterminateVisibility(true);
                    // Starts the get message feed service
                    startMessageFeedService();
                } else if (position == 4) {
                    // Gets views
                    EditText profileUserName = (EditText) findViewById(R.id.profile_user_name);
                    EditText profileEMail = (EditText) findViewById(R.id.profile_e_mail);
                    EditText profilePassword = (EditText) findViewById(R.id.profile_password);
                    EditText profileCountry = (EditText) findViewById(R.id.profile_country);
                    EditText profileCity = (EditText) findViewById(R.id.profile_city);

                    // Sets the text of the views
                    profileUserName.setText(userName);
                    profileEMail.setText(eMail);
                    profilePassword.setText(password);
                    profileCountry.setText(country);
                    profileCity.setText(city);
                }
            }
        };
    }

    // Adds filters for the response receiver
    private void addReceiverFilters() {
        // Filters for the receiver
        IntentFilter updateUserDataFilter = new IntentFilter(Constants.UPDATE_USER_DATA_RESP);
        IntentFilter addApprovingFilter = new IntentFilter(Constants.ADD_APPROVING_RESP);
        IntentFilter removeApprovingFilter = new IntentFilter(Constants.REMOVE_APPROVING_RESP);
        IntentFilter getIdeaFeedFilter = new IntentFilter(Constants.GET_IDEA_FEED_RESP);
        IntentFilter getMessageFeedFilter = new IntentFilter(Constants.GET_MESSAGE_FEED_RESP);
        IntentFilter postIdeaFilter = new IntentFilter(Constants.POST_IDEA_RESP);
        updateUserDataFilter.addCategory(Intent.CATEGORY_DEFAULT);
        addApprovingFilter.addCategory(Intent.CATEGORY_DEFAULT);
        removeApprovingFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getIdeaFeedFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getMessageFeedFilter.addCategory(Intent.CATEGORY_DEFAULT);
        postIdeaFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, updateUserDataFilter);
        registerReceiver(receiver, addApprovingFilter);
        registerReceiver(receiver, removeApprovingFilter);
        registerReceiver(receiver, getIdeaFeedFilter);
        registerReceiver(receiver, getMessageFeedFilter);
        registerReceiver(receiver, postIdeaFilter);
    }

    // Gets the user's data from the intent which
    // started this activity or from a file to
    // which the data has previously been saved
    // if no intent started the activity
    private void getUserData() {
        Intent initIntent = getIntent();
        userName = initIntent.getStringExtra(Constants.USER_NAME_KEY);
        eMail = initIntent.getStringExtra(Constants.E_MAIL_KEY);
        password = initIntent.getStringExtra(Constants.PASSWORD_KEY);
        country = initIntent.getStringExtra(Constants.COUNTRY_KEY);
        city = initIntent.getStringExtra(Constants.CITY_KEY);

        // Creates an intent used to view the detail view of an idea
        ideaDetailIntent = new Intent(this, IdeaDetailActivity.class);

        // Creates an intent used to view the conversation between two users
        conversationIntent = new Intent(this, ConversationActivity.class);

        // If using an intent to get the user name and
        // e-mail results in null-values, use the values
        // saved in the external file to get these values
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        String defaultUserName = "User Name";
        String defaultEMail = "E-Mail";
        String defaultPassword = "Password";
        String defaultCountry = "Country";
        String defaultCity = "City";
        if (userName == null) {
            userName = preferences.getString(Constants.USER_NAME_KEY, defaultUserName);
        }
        if (eMail == null) {
            eMail = preferences.getString(Constants.E_MAIL_KEY, defaultEMail);
        }
        if (password == null) {
            password = preferences.getString(Constants.PASSWORD_KEY, defaultPassword);
        }
        if (country == null) {
            country = preferences.getString(Constants.COUNTRY_KEY, defaultCountry);
        }
        if (city == null) {
            city = preferences.getString(Constants.CITY_KEY, defaultCity);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // When leaving this activity and starting a
        // new one, save the current user's username
        // and e-mail using a shared preference
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.USER_NAME_KEY, userName);
        editor.putString(Constants.E_MAIL_KEY, eMail);
        editor.putString(Constants.PASSWORD_KEY, password);
        editor.putString(Constants.COUNTRY_KEY, country);
        editor.putString(Constants.CITY_KEY, city);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void startIdeaFeedService() {
        Intent getIdeaFeedIntent = new Intent(this, GetIdeaFeedService.class);
        getIdeaFeedIntent.putExtra(Constants.USER_NAME_KEY, userName);
        startService(getIdeaFeedIntent);
    }

    private void startMessageFeedService() {
        Intent getMessageFeedIntent = new Intent(this, GetMessageFeedService.class);
        getMessageFeedIntent.putExtra(Constants.USER_NAME_KEY, userName);
        startService(getMessageFeedIntent);
    }

    private void addIdeaListClickListener() {
        final ListView recentIdeasList = (ListView) findViewById(R.id.recent_ideas);
        recentIdeasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                IdeaRecord ideaRecord = (IdeaRecord) recentIdeasList.getItemAtPosition(position);
                if (ideaRecord != null) {
                    ideaDetailIntent.putExtra(Constants.ORIGINAL_USER_KEY, userName);
                    ideaDetailIntent.putExtra(Constants.POSTER_KEY, ideaRecord.poster);
                    ideaDetailIntent.putExtra(Constants.IDEA_TEXT_KEY, ideaRecord.ideaText);
                    ideaDetailIntent.putExtra(Constants.TAG_STRING_KEY, ideaRecord.tags);
                    ideaDetailIntent.putExtra(Constants.APPROVAL_NUM_KEY, ideaRecord.approvalNum);
                    ideaDetailIntent.putExtra(Constants.IDEA_ID_KEY, ideaRecord.ideaId);
                    startActivity(ideaDetailIntent);
                }
            }
        });
    }

    private void addMessageListClickListener() {
        final ListView recentMessagesList = (ListView) findViewById(R.id.recent_messages);
        recentMessagesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MessageRecord messageRecord = (MessageRecord) recentMessagesList.getItemAtPosition(position);
                if (messageRecord != null) {
                    conversationIntent.putExtra(Constants.USER_NAME_KEY, messageRecord.sender);
                    conversationIntent.putExtra(Constants.ORIGINAL_USER_KEY, messageRecord.receiver);
                    startActivity(conversationIntent);
                }
            }
        });
    }

    public void onPostClick(View view) {
        // Get the inputs
        EditText ideaTextInput = (EditText) findViewById(R.id.idea_input);
        EditText tagInput = (EditText) findViewById(R.id.tag_input);

        // Get the status text views
        TextView positiveStatusText = (TextView) findViewById(R.id.positive_post_status_text);
        TextView negativeStatusText = (TextView) findViewById(R.id.negative_post_status_text);

        // Get the contents of the inputs
        String ideaText = String.valueOf(ideaTextInput.getText());
        String tags = String.valueOf(tagInput.getText());

        if (ideaText.equals("")) {
            positiveStatusText.setText("");
            negativeStatusText.setText("You have to write something");
        } else {
            //Hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(ideaTextInput.getWindowToken(), 0);
            // Displays the progress dialog
            progress.setTitle("Loading");
            progress.setMessage("Posting idea...");
            progress.show();
            Intent postIdeaIntent = new Intent(this, PostIdeaService.class);
            postIdeaIntent.putExtra(Constants.IDEA_TEXT_KEY, ideaText);
            postIdeaIntent.putExtra(Constants.USER_NAME_KEY, userName);
            postIdeaIntent.putExtra(Constants.TAG_STRING_KEY, tags);
            startService(postIdeaIntent);
        }
    }

    public void onUpdateClick(View view) {
        // Gets views
        EditText profileUserName = (EditText) findViewById(R.id.profile_user_name);
        EditText profileEMail = (EditText) findViewById(R.id.profile_e_mail);
        EditText profilePassword = (EditText) findViewById(R.id.profile_password);
        EditText profileCountry = (EditText) findViewById(R.id.profile_country);
        EditText profileCity = (EditText) findViewById(R.id.profile_city);

        // Gets input
        newUserName = String.valueOf(profileUserName.getText());
        newEMail = String.valueOf(profileEMail.getText());
        newPassword = String.valueOf(profilePassword.getText());
        newCountry = String.valueOf(profileCountry.getText());
        newCity = String.valueOf(profileCity.getText());

        // Starts the progress dialog
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Updating user data...");
        progress.show();

        // Creates the intent, add extras and start the service
        Intent updateUserDataIntent = new Intent(this, UpdateUserDataService.class);
        updateUserDataIntent.putExtra(Constants.ORIGINAL_USER_KEY, userName);
        updateUserDataIntent.putExtra(Constants.ORIGINAL_E_MAIL_KEY, eMail);
        updateUserDataIntent.putExtra(Constants.USER_NAME_KEY, newUserName);
        updateUserDataIntent.putExtra(Constants.E_MAIL_KEY, newEMail);
        updateUserDataIntent.putExtra(Constants.PASSWORD_KEY, newPassword);
        updateUserDataIntent.putExtra(Constants.COUNTRY_KEY, newCountry);
        updateUserDataIntent.putExtra(Constants.CITY_KEY, newCity);
        startService(updateUserDataIntent);
    }

    public void onSearchPeopleClick(View view) {
        Intent searchPeopleIntent = new Intent(this, SearchPeopleActivity.class);
        searchPeopleIntent.putExtra(Constants.ORIGINAL_USER_KEY, userName);
        startActivity(searchPeopleIntent);
    }

    public void onSearchIdeasClick(View view) {
        Intent searchIdeasIntent = new Intent(this, SearchIdeasActivity.class);
        searchIdeasIntent.putExtra(Constants.ORIGINAL_USER_KEY, userName);
        startActivity(searchIdeasIntent);
    }

    public void onTakePictureButtonClick(View view) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager pm = getPackageManager();
        if (pm != null) {
            if (takePictureIntent.resolveActivity(pm) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                // Temporarily sets the avatar image
                setAvatarImage(resultCode, data);
                break;
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                if (resultCode == RESULT_OK) {
                    // Try the request again
                }
                break;
        }
    }

    private void setAvatarImage(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                // Since the image is rotated 90 degrees when taken
                // in portrait mode, rotate it so that portrait view
                // is the standard layout to take the profile picture in
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                int imgWidth = imageBitmap.getWidth();
                int imgHeight = imageBitmap.getHeight();
                Bitmap rotatedImage = Bitmap.createBitmap(imageBitmap, 0, 0, imgWidth, imgHeight, matrix, true);

                // Sets the image button's image to the one just taken
                ImageButton avatarImageView = (ImageButton) findViewById(R.id.profile_image);
                avatarImageView.setImageBitmap(rotatedImage);
            }
        }
    }

    public void onSetCoordinatesClick(View view) {
    }

    private boolean servicesConnected() {
        // Check that google play services are available
        return true;
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (intentAction != null) {
                // If the user approved or un-approved an idea
                if (intentAction.equals(Constants.ADD_APPROVING_RESP) || intentAction.equals(Constants.REMOVE_APPROVING_RESP)) {
                    // Show a toast with the message passed with the intent
                    // This message will tell the user it either approved or un-approved the idea
                    String toastMsg = intent.getStringExtra(Constants.APPROVING_TOAST_MSG_KEY);
                    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();
                }
                // If the idea feed has been fetched from the database
                else if (intentAction.equals(Constants.GET_IDEA_FEED_RESP)) {
                    // Update the idea feed
                    updateIdeaFeed(context, intent);
                    // Hides the progress bar
                    setProgressBarIndeterminateVisibility(false);
                }
                // If the message feed has been fetched from the database
                else if (intentAction.equals(Constants.GET_MESSAGE_FEED_RESP)) {
                    // Update the message feed
                    updateMessageFeed(context, intent);
                    // Hides the progress bar
                    setProgressBarIndeterminateVisibility(false);
                }
                // If the user posted an idea
                else if (intentAction.equals(Constants.POST_IDEA_RESP)) {
                    // Take actions according to the response from the database
                    onIdeaPostResult(intent);
                }
                // If the user updated his/her user data
                else if (intentAction.equals(Constants.UPDATE_USER_DATA_RESP)) {
                    // Take actions according to the response from the database
                    onUserDataUpdatedResult(intent);

                }
            }
        }

        private void onUserDataUpdatedResult(Intent intent) {
            // Gets the response message
            String resp = intent.getStringExtra(Constants.USER_DATA_UPDATE_MSG_KEY);
            // Gets status text views
            TextView negativeStatusText = (TextView) findViewById(R.id.negative_settings_status_text);
            TextView positiveStatusText = (TextView) findViewById(R.id.positive_settings_status_text);
            // If the response is null or not success,
            // set the negative status text to corresponding message
            if (resp == null) {
                positiveStatusText.setText("");
                negativeStatusText.setText("Something went wrong");
            } else if (!resp.equals("Success")) {
                positiveStatusText.setText("");
                negativeStatusText.setText(resp);
            }
            // If the response is success
            else {
                // Set the positive status text to the response
                positiveStatusText.setText(resp);
                negativeStatusText.setText("");
                // Update the "cached" user data variables
                userName = newUserName;
                eMail = newEMail;
                password = newPassword;
                country = newCountry;
                city = newCity;
            }

            // Dismiss the progress dialog
            progress.dismiss();
        }

        private void onIdeaPostResult(Intent intent) {
            String response = intent.getStringExtra(Constants.RESPONSE_KEY);
            TextView positiveStatusText = (TextView) findViewById(R.id.positive_post_status_text);
            TextView negativeStatusText = (TextView) findViewById(R.id.negative_post_status_text);
            EditText ideaTextInput = (EditText) findViewById(R.id.idea_input);
            EditText tagInput = (EditText) findViewById(R.id.tag_input);

            if (response == null) {
                positiveStatusText.setText("");
                negativeStatusText.setText("Something went wrong");
            } else if (response.equals("Success")) {
                ideaTextInput.setText("");
                tagInput.setText("");
                positiveStatusText.setText("Idea Posted");
                negativeStatusText.setText("");
            } else {
                positiveStatusText.setText("");
                negativeStatusText.setText("Something went wrong");
            }

            progress.dismiss();
        }

        private void updateMessageFeed(Context context, Intent intent) {
            ListView recentMessagesList = (ListView) findViewById(R.id.recent_messages);
            ArrayList<MessageRecord> recentMessages = intent.getParcelableArrayListExtra(Constants.MESSAGES_KEY);
            if (recentMessages != null && recentMessagesList != null) {
                recentMessagesList.setAdapter(new MessageItemAdapter(context, R.layout.message_list_item, recentMessages));
                addMessageListClickListener();
            }
        }

        private void updateIdeaFeed(Context context, Intent intent) {
            ListView recentIdeasList = (ListView) findViewById(R.id.recent_ideas);
            ArrayList<IdeaRecord> recentIdeas = intent.getParcelableArrayListExtra(Constants.IDEAS_KEY);
            if (recentIdeas != null && recentIdeasList != null) {
                recentIdeasList.setAdapter(new IdeaItemAdapter(context, R.layout.idea_list_item, recentIdeas, userName));
                addIdeaListClickListener();
            }
        }
    }
}

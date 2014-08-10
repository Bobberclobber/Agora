package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.AddApprovingService;
import se.liu.ida.josfa969.tddd80.background_services.GetApprovingService;
import se.liu.ida.josfa969.tddd80.background_services.GetFollowingService;
import se.liu.ida.josfa969.tddd80.background_services.RemoveApprovingService;
import se.liu.ida.josfa969.tddd80.fragments.InformationFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.IdeaRecord;
import se.liu.ida.josfa969.tddd80.item_records.UserRecord;

public class InformationActivity extends Activity {
    private String userName;
    private String eMail;
    private String country;
    private String city;
    private String followers;
    private String originalUser;

    // Broadcast receiver
    private ResponseReceiver receiver;

    // Progress dialog
    public ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Intent initIntent = getIntent();
        userName = initIntent.getStringExtra(Constants.USER_NAME_KEY);
        eMail = initIntent.getStringExtra(Constants.E_MAIL_KEY);
        country = initIntent.getStringExtra(Constants.COUNTRY_KEY);
        city = initIntent.getStringExtra(Constants.CITY_KEY);
        followers = initIntent.getStringExtra(Constants.FOLLOWERS_KEY);
        originalUser = initIntent.getStringExtra(Constants.ORIGINAL_USER_KEY);

        // Filters for the receiver
        IntentFilter getFollowingFilter = new IntentFilter(Constants.GET_FOLLOWING_RESP);
        IntentFilter getApprovingFilter = new IntentFilter(Constants.GET_APPROVING_RESP);
        getFollowingFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getApprovingFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, getFollowingFilter);
        registerReceiver(receiver, getApprovingFilter);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Fetching data...");

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new InformationFragment()).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("----------");
        System.out.println("On Resume");
        System.out.println("----------");

        // Shows the progress dialog
        progress.show();

        // Gets views
        TextView userNameView = (TextView) findViewById(R.id.other_user_user_name);
        TextView eMailView = (TextView) findViewById(R.id.other_user_e_mail);
        TextView countryView = (TextView) findViewById(R.id.other_user_country);
        TextView cityView = (TextView) findViewById(R.id.other_user_city);
        TextView followersView = (TextView) findViewById(R.id.information_other_user_follower_number);

        // Sets the content
        userNameView.setText(userName);
        eMailView.setText(eMail);
        countryView.setText(country);
        cityView.setText(city);
        followersView.setText(followers);

        // Starts the get following and get approving service
        Intent getFollowingIntent = new Intent(this, GetFollowingService.class);
        Intent getApprovingIntent = new Intent(this, GetApprovingService.class);
        getFollowingIntent.putExtra(Constants.USER_NAME_KEY, userName);
        getApprovingIntent.putExtra(Constants.USER_NAME_KEY, userName);
        startService(getFollowingIntent);
        startService(getApprovingIntent);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    private class OnFollowingUserClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            view.setBackgroundResource(R.color.clicked_item_background);
            TextView userNameView = (TextView) view.findViewById(R.id.user_list_user_name);
            String clickedUserName = String.valueOf(userNameView.getText());
            if (!clickedUserName.equals(originalUser)) {
                ArrayList<String> userData = JsonMethods.getUserData(clickedUserName);
                onListItemClick(userData);
            }
        }
    }

    private class OnApprovingIdeaClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            view.setBackgroundResource(R.color.clicked_item_background);
            TextView posterView = (TextView) view.findViewById(R.id.poster);
            String clickedUserName = String.valueOf(posterView.getText());
            if (!clickedUserName.equals(originalUser)) {
                ArrayList<String> userData = JsonMethods.getUserData(clickedUserName);
                onListItemClick(userData);
            }
        }
    }

    public void onListItemClick(ArrayList<String> userData) {
        Intent otherProfileIntent = new Intent(this, OtherProfileActivity.class);

        // Gets basic data of the user whose idea
        // was clicked (excluding the users which
        // the user whose idea was clicked is following)
        String clickedUserName = userData.get(0);
        String clickedEMail = userData.get(1);
        String clickedCountry = userData.get(2);
        String clickedCity = userData.get(3);
        String clickedFollowers = userData.get(4);

        // Attaches the basic data to the intent
        otherProfileIntent.putExtra(Constants.USER_NAME_KEY, clickedUserName);
        otherProfileIntent.putExtra(Constants.E_MAIL_KEY, clickedEMail);
        otherProfileIntent.putExtra(Constants.COUNTRY_KEY, clickedCountry);
        otherProfileIntent.putExtra(Constants.CITY_KEY, clickedCity);
        otherProfileIntent.putExtra(Constants.FOLLOWERS_KEY, clickedFollowers);
        otherProfileIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);

        // Starts the new activity
        startActivity(otherProfileIntent);
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("----------");
            System.out.println("On Receive");
            System.out.println("----------");

            String intentAction = intent.getAction();
            if (intentAction != null) {
                if (intentAction.equals(Constants.GET_FOLLOWING_RESP)) {
                    ArrayList<UserRecord> followingList = intent.getParcelableArrayListExtra(Constants.PEOPLE_KEY);
                    LinearLayout followingListView = (LinearLayout) findViewById(R.id.other_user_following_list);

                    // Adds user list items to the linear layout
                    if (followingList != null) {
                        for (UserRecord userRecord : followingList) {
                            // Creates a new user list item
                            createUserListItem(followingListView, userRecord);
                        }
                    }
                } else {
                    ArrayList<IdeaRecord> approvingList = intent.getParcelableArrayListExtra(Constants.IDEAS_KEY);
                    LinearLayout approvingListView = (LinearLayout) findViewById(R.id.other_user_approving_list);

                    // Adds idea list items to the linear layout
                    if (approvingList != null) {
                        for (IdeaRecord ideaRecord : approvingList) {
                            // Creates a new idea list item
                            createIdeaListItem(approvingListView, ideaRecord);
                        }
                    }
                    progress.dismiss();
                }
            }
        }

        private void createIdeaListItem(LinearLayout approvingListView, final IdeaRecord ideaRecord) {
            final View temp = getLayoutInflater().inflate(R.layout.idea_list_item, null);
            if (temp != null) {

                // Gets views
                TextView posterView = (TextView) temp.findViewById(R.id.poster);
                TextView ideaTextView = (TextView) temp.findViewById(R.id.idea_text);
                TextView tagsView = (TextView) temp.findViewById(R.id.tags);
                TextView approvalNumView = (TextView) temp.findViewById(R.id.approval_num);
                TextView ideaIdView = (TextView) temp.findViewById(R.id.idea_id);

                // Sets texts
                posterView.setText(ideaRecord.poster);
                ideaTextView.setText(ideaRecord.ideaText);
                String tagString = "";
                for (Object tag : ideaRecord.tags) {
                    tagString += "#" + tag + " ";
                }
                tagsView.setText(tagString);
                approvalNumView.setText(ideaRecord.approvalNum);
                ideaIdView.setText(ideaRecord.ideaId);

                // Adds the item to the list
                approvingListView.addView(temp);

                // Adds on click listener
                temp.setOnClickListener(new OnApprovingIdeaClickListener());

                // Gets the button views
                final Button approvalButton = (Button) temp.findViewById(R.id.approval_button);
                final Button unApprovalButton = (Button) temp.findViewById(R.id.un_approval_button);

                // Adds onClickListener to the approval button
                approvalButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Temporarily increases the approval number
                        TextView approvalNum = (TextView) temp.findViewById(R.id.approval_num);
                        String approvalNumString = String.valueOf(approvalNum.getText());
                        int incApprovalNum = Integer.parseInt(approvalNumString) + 1;
                        approvalNum.setText(String.valueOf(incApprovalNum));

                        // Sets the buttons' visibility
                        approvalButton.setVisibility(View.GONE);
                        unApprovalButton.setVisibility(View.VISIBLE);

                        // Starts a service to add the clicked idea's ID to the user's approving list
                        Intent addApprovingIntent = new Intent(getBaseContext(), AddApprovingService.class);
                        addApprovingIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
                        addApprovingIntent.putExtra(Constants.IDEA_ID_KEY, ideaRecord.ideaId);
                        startService(addApprovingIntent);
                    }
                });

                // Adds onClickListener to the un-approval button
                unApprovalButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Temporarily increases the approval number
                        TextView approvalNum = (TextView) temp.findViewById(R.id.approval_num);
                        String approvalNumString = String.valueOf(approvalNum.getText());
                        int decApprovalNum = Integer.parseInt(approvalNumString) - 1;
                        approvalNum.setText(String.valueOf(decApprovalNum));

                        // Sets the buttons' visibility
                        approvalButton.setVisibility(View.VISIBLE);
                        unApprovalButton.setVisibility(View.GONE);

                        // Starts a service to remove the clicked idea's ID from the user's approving list
                        Intent removeApprovingIntent = new Intent(getBaseContext(), RemoveApprovingService.class);
                        removeApprovingIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
                        removeApprovingIntent.putExtra(Constants.IDEA_ID_KEY, ideaRecord.ideaId);
                        startService(removeApprovingIntent);
                    }
                });

                // Displays the correct button depending on
                // if the user is approving the idea or not
                if (JsonMethods.userIsApproving(originalUser, ideaRecord.ideaId)) {
                    approvalButton.setVisibility(View.GONE);
                    unApprovalButton.setVisibility(View.VISIBLE);
                } else {
                    approvalButton.setVisibility(View.VISIBLE);
                    unApprovalButton.setVisibility(View.GONE);
                }
            }
        }

        private void createUserListItem(LinearLayout followingListView, UserRecord userRecord) {
            View temp = getLayoutInflater().inflate(R.layout.user_list_item, null);
            if (temp != null) {

                // Gets views
                TextView userNameView = (TextView) temp.findViewById(R.id.user_list_user_name);
                TextView eMailView = (TextView) temp.findViewById(R.id.user_list_e_mail);
                TextView followerNumView = (TextView) temp.findViewById(R.id.user_list_follower_num);
                TextView countryView = (TextView) temp.findViewById(R.id.user_list_country);
                TextView cityView = (TextView) temp.findViewById(R.id.user_list_city);

                // Sets texts
                userNameView.setText(userRecord.userName);
                eMailView.setText(userRecord.eMail);
                followerNumView.setText(userRecord.followers);
                countryView.setText(userRecord.country);
                cityView.setText(userRecord.city);

                // Adds the item to the list
                followingListView.addView(temp);

                // Adds on click listener
                temp.setOnClickListener(new OnFollowingUserClickListener());
            }
        }
    }
}

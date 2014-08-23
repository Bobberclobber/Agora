package se.liu.ida.josfa969.tddd80.list_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.item_records.UserRecord;

/**
 * Created by Josef on 03/08/14.
 *
 * An adapter used to display user records in a list view
 */
public class UserItemAdapter extends ArrayAdapter<UserRecord> {
    private ArrayList<UserRecord> userRecords;
    private Context context;

    public  UserItemAdapter(Context context, int textViewSourceId, ArrayList<UserRecord> userRecords) {
        super(context, textViewSourceId, userRecords);
        this.userRecords = userRecords;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.user_list_item, null);
        }

        UserRecord userRecord = userRecords.get(position);
        if (userRecord != null && v != null) {
            TextView userName = (TextView) v.findViewById(R.id.user_list_user_name);
            TextView eMail = (TextView) v.findViewById(R.id.user_list_e_mail);
            TextView country = (TextView) v.findViewById(R.id.user_list_country);
            TextView city = (TextView) v.findViewById(R.id.user_list_city);
            TextView followerNum = (TextView) v.findViewById(R.id.user_list_follower_num);
            ImageView avatarImage = (ImageView) v.findViewById(R.id.user_avatar_image);

            if (userName != null) {
                userName.setText(userRecord.userName);
            }
            if (eMail != null) {
                eMail.setText(userRecord.eMail);
            }
            if (country != null) {
                country.setText(userRecord.country);
            }
            if (city != null) {
                city.setText(userRecord.city);
            }
            if (followerNum != null) {
                followerNum.setText(userRecord.followers);
            }
            if (avatarImage != null) {
                avatarImage.setImageBitmap(Constants.stringToBitmap(userRecord.image));
            }
        }
        return v;
    }
}

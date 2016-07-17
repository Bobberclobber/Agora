package se.liu.ida.josfa969.tddd80.list_adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.item_records.CommentRecord;

/**
 * Created by Josef on 10/08/14.
 *
 * An adapter used to display comment records in a list view
 */
public class CommentItemAdapter extends ArrayAdapter<CommentRecord> {
    private ArrayList<CommentRecord> commentRecords;
    private Context context;

    public CommentItemAdapter(Context context, int textViewSourceId, ArrayList<CommentRecord> commentRecords) {
        super(context, textViewSourceId, commentRecords);
        this.commentRecords = commentRecords;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.comment_list_item, null);
        }

        CommentRecord commentRecord = commentRecords.get(position);
        if (commentRecord != null && v != null) {
            TextView user = (TextView) v.findViewById(R.id.comment_user_name);
            TextView commentText = (TextView) v.findViewById(R.id.comment_text);
            ImageView avatarImage = (ImageView) v.findViewById(R.id.comment_avatar_image);

            if (user != null) {
                user.setText(commentRecord.user);
            }
            if (commentText != null) {
                commentText.setText(commentRecord.commentText);
            }
            if (avatarImage != null) {
                avatarImage.setImageBitmap(Constants.stringToBitmap(commentRecord.image));
            }
        }
        return v;
    }
}

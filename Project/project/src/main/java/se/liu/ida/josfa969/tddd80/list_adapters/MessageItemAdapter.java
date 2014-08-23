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
import se.liu.ida.josfa969.tddd80.item_records.MessageRecord;

/**
 * Created by Josef on 30/07/14.
 *
 * An adapter used to display message records in a list view
 */
public class MessageItemAdapter extends ArrayAdapter<MessageRecord> {
    private ArrayList<MessageRecord> messageRecords;
    private Context context;

    public MessageItemAdapter(Context context, int textViewSourceId, ArrayList<MessageRecord> messageRecords) {
        super(context, textViewSourceId, messageRecords);
        this.messageRecords = messageRecords;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.message_list_item, null);
        }

        MessageRecord messageRecord = messageRecords.get(position);
        if (messageRecord != null && v != null) {
            TextView sender = (TextView) v.findViewById(R.id.sender);
            TextView receiver = (TextView) v.findViewById(R.id.receiver);
            TextView messageText = (TextView) v.findViewById(R.id.message_text);
            ImageView avatarImage = (ImageView) v.findViewById(R.id.message_avatar_image);

            if (sender != null) {
                sender.setText(messageRecord.sender);
            }
            if (receiver != null) {
                receiver.setText(messageRecord.receiver);
            }
            if (messageText != null) {
                messageText.setText(messageRecord.messageText);
            }
            if (avatarImage != null) {
                avatarImage.setImageBitmap(Constants.stringToBitmap(messageRecord.image));
            }
        }
        return v;
    }
}

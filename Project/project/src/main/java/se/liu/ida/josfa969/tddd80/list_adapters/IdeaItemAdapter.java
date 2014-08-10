package se.liu.ida.josfa969.tddd80.list_adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.background_services.AddApprovingService;
import se.liu.ida.josfa969.tddd80.background_services.RemoveApprovingService;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;
import se.liu.ida.josfa969.tddd80.help_classes.JsonMethods;
import se.liu.ida.josfa969.tddd80.item_records.IdeaRecord;

/**
 * Created by Josef on 28/07/14.
 *
 * An adapter used to display idea records in a list view
 */
public class IdeaItemAdapter extends ArrayAdapter<IdeaRecord> {
    private ArrayList<IdeaRecord> ideaRecords;
    private Context context;
    private String originalUser;

    public IdeaItemAdapter(Context context, int textViewSourceId, ArrayList<IdeaRecord> ideaRecords, String originalUser) {
        super(context, textViewSourceId, ideaRecords);
        this.ideaRecords = ideaRecords;
        this.context = context;
        this.originalUser = originalUser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView poster;
        TextView ideaText;
        TextView ideaId;
        TextView approvalNum;
        TextView tags;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.idea_list_item, null);
        }

        final IdeaRecord ideaRecord = ideaRecords.get(position);
        if (ideaRecord != null && v != null) {
            poster = (TextView) v.findViewById(R.id.poster);
            ideaText = (TextView) v.findViewById(R.id.idea_text);
            ideaId = (TextView) v.findViewById(R.id.idea_id);
            approvalNum = (TextView) v.findViewById(R.id.approval_num);
            tags = (TextView) v.findViewById(R.id.tags);

            if (poster != null) {
                poster.setText(ideaRecord.poster);
            }

            if (ideaText != null) {
                ideaText.setText(ideaRecord.ideaText);
            }

            if (ideaId != null) {
                ideaId.setText(ideaRecord.ideaId);
            }

            if (approvalNum != null) {
                approvalNum.setText(ideaRecord.approvalNum);
            }
            if (tags != null) {
                String tagString = "";
                for (Object tag : ideaRecord.tags) {
                    tagString += "#" + tag + " ";
                }
                tags.setText(tagString);
            }

            // Gets the button views
            final Button approvalButton = (Button) v.findViewById(R.id.approval_button);
            final Button unApprovalButton = (Button) v.findViewById(R.id.un_approval_button);

            // Displays the correct button depending on
            // if the user is approving the idea or not
            if (JsonMethods.userIsApproving(originalUser, ideaRecord.ideaId)) {
                approvalButton.setVisibility(View.GONE);
                unApprovalButton.setVisibility(View.VISIBLE);
            } else {
                approvalButton.setVisibility(View.VISIBLE);
                unApprovalButton.setVisibility(View.GONE);
            }

            // Creates a temporary view
            final View tempView = v;

            // Adds onClickListener to the approval button
            approvalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Temporarily increases the approval number
                    TextView approvalNum = (TextView) tempView.findViewById(R.id.approval_num);
                    String approvalNumString = String.valueOf(approvalNum.getText());
                    int incApprovalNum = Integer.parseInt(approvalNumString) + 1;
                    approvalNum.setText(String.valueOf(incApprovalNum));

                    // Sets the buttons' visibility
                    approvalButton.setVisibility(View.GONE);
                    unApprovalButton.setVisibility(View.VISIBLE);

                    // Starts a service to add the clicked idea's ID to the user's approving list
                    Intent addApprovingIntent = new Intent(context, AddApprovingService.class);
                    addApprovingIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
                    addApprovingIntent.putExtra(Constants.IDEA_ID_KEY, ideaRecord.ideaId);
                    context.startService(addApprovingIntent);
                }
            });

            // Adds onClickListener to the un-approval button
            unApprovalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Temporarily increases the approval number
                    TextView approvalNum = (TextView) tempView.findViewById(R.id.approval_num);
                    String approvalNumString = String.valueOf(approvalNum.getText());
                    int decApprovalNum = Integer.parseInt(approvalNumString) - 1;
                    approvalNum.setText(String.valueOf(decApprovalNum));

                    // Sets the buttons' visibility
                    approvalButton.setVisibility(View.VISIBLE);
                    unApprovalButton.setVisibility(View.GONE);

                    // Starts a service to remove the clicked idea's ID from the user's approving list
                    Intent removeApprovingIntent = new Intent(context, RemoveApprovingService.class);
                    removeApprovingIntent.putExtra(Constants.ORIGINAL_USER_KEY, originalUser);
                    removeApprovingIntent.putExtra(Constants.IDEA_ID_KEY, ideaRecord.ideaId);
                    context.startService(removeApprovingIntent);
                }
            });
        }
        return v;
    }
}

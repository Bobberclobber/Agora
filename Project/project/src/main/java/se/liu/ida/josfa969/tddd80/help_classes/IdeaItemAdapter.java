package se.liu.ida.josfa969.tddd80.help_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.help_classes.IdeaRecord;
import se.liu.ida.josfa969.tddd80.R;

/**
 * Created by Josef on 28/07/14.
 *
 * An adapter used to display idea records in a list view
 */
public class IdeaItemAdapter extends ArrayAdapter<IdeaRecord> {
    private ArrayList<IdeaRecord> ideaRecords;
    private Context context;

    public IdeaItemAdapter(Context context, int textViewSourceId, ArrayList<IdeaRecord> ideaRecords) {
        super(context, textViewSourceId, ideaRecords);
        this.ideaRecords = ideaRecords;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.idea_list_item, null);
        }

        IdeaRecord ideaRecord = ideaRecords.get(position);
        if (ideaRecord != null && v != null) {
            TextView poster = (TextView) v.findViewById(R.id.poster);
            TextView ideaText = (TextView) v.findViewById(R.id.idea_text);
            TextView ideaId = (TextView) v.findViewById(R.id.idea_id);
            TextView approvalNum = (TextView) v.findViewById(R.id.approval_num);

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
        }
        return v;
    }
}

package se.liu.ida.josfa969.tddd80.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import se.liu.ida.josfa969.tddd80.IdeaRecord;
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

        IdeaRecord idea = ideaRecords.get(position);
        if (idea != null && v != null) {
            TextView poster = (TextView) v.findViewById(R.id.poster);
            TextView ideaText = (TextView) v.findViewById(R.id.idea_text);

            if (poster != null) {
                poster.setText(idea.poster);
            }

            if (ideaText != null) {
                ideaText.setText(idea.ideaText);
            }
        }
        return v;
    }
}

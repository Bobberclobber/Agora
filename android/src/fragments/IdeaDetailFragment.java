package se.liu.ida.josfa969.tddd80.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.liu.ida.josfa969.tddd80.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class IdeaDetailFragment extends Fragment {

    public IdeaDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_idea_detail, container, false);
    }
}

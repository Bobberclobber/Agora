package se.liu.ida.josfa969.tddd80.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.liu.ida.josfa969.tddd80.R;

/**
 * A fragment used to display the user's own profile page
 */
public class IdeaFeedFragment extends Fragment {

    public IdeaFeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_idea_feed, container, false);
    }
}

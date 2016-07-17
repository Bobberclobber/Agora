package se.liu.ida.josfa969.tddd80.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.liu.ida.josfa969.tddd80.R;

/**
 * A fragment used when posting an idea
 */
public class PostIdeaFragment extends Fragment {

    public PostIdeaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_idea, container, false);
    }
}

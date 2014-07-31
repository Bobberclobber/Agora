package se.liu.ida.josfa969.tddd80.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.liu.ida.josfa969.tddd80.R;

/**
 * A fragment used for login in, creating a new account or previewing the app
 */
public class InitialFragment extends Fragment {

    public InitialFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_initial, container, false);
    }
}

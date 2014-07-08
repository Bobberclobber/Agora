package se.liu.ida.josfa969.tddd80.fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import se.liu.ida.josfa969.tddd80.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class ProfileSettingsFragment extends Fragment {

    public ProfileSettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_settings, container, false);
    }
}

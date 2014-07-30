package se.liu.ida.josfa969.tddd80.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.liu.ida.josfa969.tddd80.R;

/**
 * A fragment used to type and send messages
 */
public class SendMessageFragment extends Fragment {

    public SendMessageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_message, container, false);
    }
}

package com.example.lab4.lab4android;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventLoggerFragment extends Fragment {


    public EventLoggerFragment() {

    }

    private void onContactWithBeacon(UUID uuid) {


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_logger, container, false);
    }

   /* private boolean isUUIDValid(UUID uuid) {
        return this.getContext().
            getSharedPreferences(BeaconListFragment.BEACONS_LIST, Context.MODE_PRIVATE).
            contains(uuid.toString());
    }*/

}

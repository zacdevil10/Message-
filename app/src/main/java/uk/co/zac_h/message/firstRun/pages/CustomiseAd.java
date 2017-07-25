package uk.co.zac_h.message.firstRun.pages;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.zac_h.message.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomiseAd extends Fragment {

    //TODO: REQUEST CONTACTS PERMISSIONS

    public CustomiseAd() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tutorial_page, container, false);
    }

}

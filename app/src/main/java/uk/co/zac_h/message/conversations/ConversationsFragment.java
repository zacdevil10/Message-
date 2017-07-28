package uk.co.zac_h.message.conversations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.zac_h.message.R;
import uk.co.zac_h.message.common.sync.SyncLatestMessages;

public class ConversationsFragment extends Fragment {

    BroadcastReceiver getLatestSMS;

    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        final RecyclerView latestMessagesList = (RecyclerView) view.findViewById(R.id.latestMessagesList);
        latestMessagesList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        latestMessagesList.setLayoutManager(layoutManager);

        new SyncLatestMessages(getContext(), latestMessagesList).execute();

        getLatestSMS = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new SyncLatestMessages(getContext(), latestMessagesList).execute();
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(getLatestSMS, new IntentFilter("sms.latest"));

        return view;
    }

}

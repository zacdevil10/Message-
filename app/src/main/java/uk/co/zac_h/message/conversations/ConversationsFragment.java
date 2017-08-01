package uk.co.zac_h.message.conversations;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.zac_h.message.R;
import uk.co.zac_h.message.common.sync.RefreshLatestMessages;
import uk.co.zac_h.message.common.sync.SyncLatestMessages;
import uk.co.zac_h.message.common.sync.UpdateContactId;
import uk.co.zac_h.message.database.ReturnData;

public class ConversationsFragment extends Fragment{

    private RecyclerView latestMessagesList;

    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        latestMessagesList = (RecyclerView) view.findViewById(R.id.latestMessagesList);
        latestMessagesList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        latestMessagesList.setLayoutManager(layoutManager);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) new SyncLatestMessages(getContext(), latestMessagesList).execute();

        BroadcastReceiver updateView = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new SyncLatestMessages(getContext(), latestMessagesList).execute();
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(updateView, new IntentFilter("sms.received"));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new SyncLatestMessages(getContext(), latestMessagesList).execute();
    }
}

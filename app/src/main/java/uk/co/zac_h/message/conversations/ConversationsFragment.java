package uk.co.zac_h.message.conversations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.zac_h.message.R;
import uk.co.zac_h.message.conversations.conversationsadapter.ConversationsAdapter;

public class ConversationsFragment extends Fragment {


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
        final ConversationsAdapter conversationsAdapter = new ConversationsAdapter(getActivity());

        latestMessagesList.setAdapter(conversationsAdapter);

        return view;
    }

}

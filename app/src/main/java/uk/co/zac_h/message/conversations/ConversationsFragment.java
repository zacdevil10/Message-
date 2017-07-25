package uk.co.zac_h.message.conversations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uk.co.zac_h.message.R;
import uk.co.zac_h.message.conversations.conversationsadapter.ConversationsAdapter;
import uk.co.zac_h.message.database.DatabaseHelper;
import uk.co.zac_h.message.database.databaseModel.MessageModel;
import uk.co.zac_h.message.database.databaseModel.ProfileModel;

public class ConversationsFragment extends Fragment {

    private DatabaseHelper db;

    private final ArrayList<String> number = new ArrayList<>();
    private final ArrayList<String> body = new ArrayList<>();

    public ConversationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        db = new DatabaseHelper(getActivity());
        List<MessageModel> messageModels = db.getLatestMessages();
        for (MessageModel messageModel: messageModels) {
            number.add(messageModel.getNumber());
            body.add(messageModel.getBody());
        }

        final RecyclerView latestMessagesList = (RecyclerView) view.findViewById(R.id.latestMessagesList);
        latestMessagesList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        latestMessagesList.setLayoutManager(layoutManager);
        final ConversationsAdapter conversationsAdapter = new ConversationsAdapter(getActivity(), number, body);

        latestMessagesList.setAdapter(conversationsAdapter);

        return view;
    }

    public void requestMessages() {

    }

}

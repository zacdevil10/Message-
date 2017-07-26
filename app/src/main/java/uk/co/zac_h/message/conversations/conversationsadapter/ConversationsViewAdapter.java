package uk.co.zac_h.message.conversations.conversationsadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.zac_h.message.R;

public class ConversationsViewAdapter extends RecyclerView.Adapter<ConversationsViewAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList body;
    private final ArrayList date;
    private final ArrayList read;
    private final ArrayList messageType;

    public ConversationsViewAdapter(Context context, ArrayList body, ArrayList date, ArrayList read, ArrayList messageType) {
        this.context = context;
        this.body = body;
        this.date = date;
        this.read = read;
        this.messageType = messageType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_left, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_right, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.contentView.setText(body.get(position).toString());

        System.out.println("Message Type: " + messageType.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return date.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageType.get(position).toString().equals("1")) return 1;
        else return 2;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView contentView;

        ViewHolder(View itemView) {
            super(itemView);
            contentView = (TextView) itemView.findViewById(R.id.message_content);
        }
    }
}

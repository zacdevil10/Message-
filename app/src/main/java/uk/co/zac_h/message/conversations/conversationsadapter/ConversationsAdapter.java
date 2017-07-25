package uk.co.zac_h.message.conversations.conversationsadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.zac_h.message.R;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList number;
    private final ArrayList body;

    public ConversationsAdapter(Context context, ArrayList number, ArrayList body) {
        this.context = context;
        this.number = number;
        this.body = body;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.number.setText(number.get(position).toString());
        holder.body.setText(body.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return number.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView number;
        final TextView body;

        ViewHolder(View itemView) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.number);
            body = (TextView) itemView.findViewById(R.id.body);
        }
    }
}

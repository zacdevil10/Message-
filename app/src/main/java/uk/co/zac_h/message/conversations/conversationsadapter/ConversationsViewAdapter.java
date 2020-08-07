package uk.co.zac_h.message.conversations.conversationsadapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.zac_h.message.R;

public class ConversationsViewAdapter extends RecyclerView.Adapter<ConversationsViewAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList body;
    private final ArrayList date;
    private final ArrayList read;
    private final ArrayList messageType;
    private final int color;
    private final ArrayList<Boolean> animation;

    private int lastPosition = -1;

    public ConversationsViewAdapter(Context context, ArrayList body, ArrayList date, ArrayList read, ArrayList messageType, int color, ArrayList<Boolean> animation) {
        this.context = context;
        this.body = body;
        this.date = date;
        this.read = read;
        this.messageType = messageType;
        this.color = color;
        this.animation = animation;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_left, parent, false);
            view.findViewById(R.id.parent).setBackgroundColor(color);
            //TODO: Use drawable with rounded corners
            //DrawableCompat.setTint(context.getResources().getDrawable(R.drawable.rounded_background_left, null), color);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_right, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.contentView.setText(body.get(position).toString());

        if (animation.get(position)) setAnimation(holder.itemView, position);
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_bottom);
            view.startAnimation(animation);
            lastPosition = position;
        }
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

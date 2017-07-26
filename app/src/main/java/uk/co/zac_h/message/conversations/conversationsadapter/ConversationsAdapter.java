package uk.co.zac_h.message.conversations.conversationsadapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.zac_h.message.R;
import uk.co.zac_h.message.conversations.ConversationView;
import uk.co.zac_h.message.photos.LetterTitleProvider;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList number;
    private final ArrayList name;
    private final ArrayList body;
    private final ArrayList type;
    private final ArrayList timeStamp;

    public ConversationsAdapter(Context context, ArrayList number, ArrayList name, ArrayList body, ArrayList type, ArrayList timeStamp) {
        this.context = context;
        this.number = number;
        this.name = name;
        this.body = body;
        this.type = type;
        this.timeStamp = timeStamp;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(name.get(position).toString());

        if (type.get(position).toString().equals("1")) {
            holder.body.setText(body.get(position).toString());
        } else {
            holder.body.setText("You: " + body.get(position).toString());
        }

        holder.timeStamp.setText(timeStamp.get(position).toString());

        final Resources resources = context.getResources();
        final int titleSize = resources.getDimensionPixelSize(R.dimen.letter_tile_size);
        final LetterTitleProvider letterTitleProvider = new LetterTitleProvider(context);
        final Bitmap image = letterTitleProvider.getLetterTile(name.get(position).toString(), name.get(position).toString(), titleSize, titleSize);

        holder.imageView.setImageBitmap(image);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ConversationView.class);
                intent.putExtra("name", name.get(position).toString());
                intent.putExtra("number", number.get(position).toString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView name;
        final TextView body;
        final TextView timeStamp;
        final CircleImageView imageView;
        final ConstraintLayout item;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            body = (TextView) itemView.findViewById(R.id.body);
            timeStamp = (TextView) itemView.findViewById(R.id.timeStamp);
            imageView = (CircleImageView) itemView.findViewById(R.id.image);
            item = (ConstraintLayout) itemView.findViewById(R.id.item);
        }
    }
}

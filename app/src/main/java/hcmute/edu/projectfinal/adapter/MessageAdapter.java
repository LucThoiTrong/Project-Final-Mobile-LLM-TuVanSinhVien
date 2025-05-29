package hcmute.edu.projectfinal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import hcmute.edu.projectfinal.R;
import hcmute.edu.projectfinal.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final List<Message> messageList;
    private static final int MARGIN_ICON_TEXT_DP = 8;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageIcon;
        ConstraintLayout constraintLayout;
        Context context;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageIcon = itemView.findViewById(R.id.messageIcon);
            constraintLayout = (ConstraintLayout) itemView;
        }

        private int dpToPx() {
            return (int) (MessageAdapter.MARGIN_ICON_TEXT_DP * context.getResources().getDisplayMetrics().density);
        }

        void bind(Message message) {
            messageTextView.setText(message.getContent());

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            // Reset constraints that will be changed to avoid conflicts
            constraintSet.clear(R.id.messageTextView, ConstraintSet.START);
            constraintSet.clear(R.id.messageTextView, ConstraintSet.END);
            constraintSet.clear(R.id.messageIcon, ConstraintSet.START);
            constraintSet.clear(R.id.messageIcon, ConstraintSet.END);

            // Common: Constrain width for messageTextView
            constraintSet.constrainWidth(R.id.messageTextView, ConstraintSet.WRAP_CONTENT);

            if ("user".equalsIgnoreCase(message.getRole())) {
                messageTextView.setBackgroundResource(R.drawable.message_user_background);
                messageTextView.setTextColor(ContextCompat.getColor(context, R.color.user_message_text_color));
                messageIcon.setImageResource(R.drawable.ic_user); // Đảm bảo bạn có icon R.drawable.ic_user

                // 1. Icon to the far right of the parent
                constraintSet.connect(R.id.messageIcon, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                // 2. TextView to the left of the Icon
                constraintSet.connect(R.id.messageTextView, ConstraintSet.END, R.id.messageIcon, ConstraintSet.START, dpToPx());
                // TextView starts from parent start (allows it to take available space)
                constraintSet.connect(R.id.messageTextView, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                // Bias the TextView towards the right, making it appear next to the icon
                constraintSet.setHorizontalBias(R.id.messageTextView, 1.0f);

                constraintSet.setVisibility(R.id.messageIcon, View.VISIBLE);

            } else if ("assistant".equalsIgnoreCase(message.getRole())) {
                messageTextView.setBackgroundResource(R.drawable.message_bot_background);
                messageTextView.setTextColor(ContextCompat.getColor(context, R.color.assistant_message_text_color));
                messageIcon.setImageResource(R.drawable.ic_bot); // Đảm bảo bạn có icon R.drawable.ic_bot

                // Assistant: [Icon] [Text] <---- Aligned to the Parent LEFT
                // 1. Icon to the far left of the parent
                constraintSet.connect(R.id.messageIcon, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

                // 2. TextView to the right of the Icon
                constraintSet.connect(R.id.messageTextView, ConstraintSet.START, R.id.messageIcon, ConstraintSet.END, dpToPx());
                // TextView ends at parent end (allows it to take available space)
                constraintSet.connect(R.id.messageTextView, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                // Bias the TextView towards the left, making it appear next to the icon
                constraintSet.setHorizontalBias(R.id.messageTextView, 0.0f);

                constraintSet.setVisibility(R.id.messageIcon, View.VISIBLE);
            } else {
                // Optional: handle other roles or default case
                constraintSet.setVisibility(R.id.messageIcon, View.GONE); // Hide icon for unknown roles
                constraintSet.connect(R.id.messageTextView, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.connect(R.id.messageTextView, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                constraintSet.setHorizontalBias(R.id.messageTextView, 0.0f);
            }
            constraintSet.applyTo(constraintLayout);
        }
    }
}
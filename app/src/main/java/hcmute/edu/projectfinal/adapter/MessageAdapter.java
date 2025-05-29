package hcmute.edu.projectfinal.adapter;

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

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageIcon = itemView.findViewById(R.id.messageIcon);
            constraintLayout = (ConstraintLayout) itemView;
        }

        void bind(Message message) {
            messageTextView.setText(message.getContent());

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            if ("user".equalsIgnoreCase(message.getRole())) {
                messageTextView.setBackgroundResource(R.drawable.message_user_background);
                // Sử dụng màu từ R.color
                messageTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.user_message_text_color));
                messageIcon.setImageResource(R.drawable.ic_user); // Đảm bảo bạn có icon này

                // Căn chỉnh cho tin nhắn người dùng (sang phải)
                constraintSet.clear(R.id.messageTextView, ConstraintSet.START);
                constraintSet.connect(R.id.messageTextView, ConstraintSet.END,
                        ConstraintSet.PARENT_ID, ConstraintSet.END, 16); // Tăng margin nếu cần

                constraintSet.clear(R.id.messageIcon, ConstraintSet.START);
                constraintSet.connect(R.id.messageIcon, ConstraintSet.END,
                        R.id.messageTextView, ConstraintSet.START, 8);

                // Đảm bảo icon và text view được căn chỉnh đúng với vai trò user
                constraintSet.setVisibility(R.id.messageIcon, View.VISIBLE); // Hiển thị icon user

            } else if ("assistant".equalsIgnoreCase(message.getRole())) {
                messageTextView.setBackgroundResource(R.drawable.message_bot_background);
                // Sử dụng màu từ R.color
                messageTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.assistant_message_text_color));
                messageIcon.setImageResource(R.drawable.ic_bot); // Đảm bảo bạn có icon này

                // Căn chỉnh cho tin nhắn trợ lý (sang trái)
                constraintSet.clear(R.id.messageTextView, ConstraintSet.END);
                constraintSet.connect(R.id.messageTextView, ConstraintSet.START,
                        R.id.messageIcon, ConstraintSet.END, 8);

                constraintSet.clear(R.id.messageIcon, ConstraintSet.END);
                constraintSet.connect(R.id.messageIcon, ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.START, 16); // Tăng margin nếu cần

                // Đảm bảo icon và text view được căn chỉnh đúng với vai trò assistant
                constraintSet.setVisibility(R.id.messageIcon, View.VISIBLE); // Hiển thị icon bot

            }
            constraintSet.applyTo(constraintLayout);
        }
    }
}
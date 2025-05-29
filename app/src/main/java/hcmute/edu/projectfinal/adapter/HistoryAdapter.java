package hcmute.edu.projectfinal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import hcmute.edu.projectfinal.R;
import hcmute.edu.projectfinal.model.ChatData;
import hcmute.edu.projectfinal.model.ChatHistory;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ChatViewHolder> {
    private final List<ChatHistory> chatHistoryList;
    // Listener để xử lý nút delete
    public interface OnClickListener {
        void onDeleteClick(View v, int position);
    }

    private final OnClickListener listener;

    public HistoryAdapter(List<ChatHistory> chatHistories, OnClickListener listener) {
        this.chatHistoryList = chatHistories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_history, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatHistory chatHistory = ChatData.chatHistory.get(position);

        // Hiển thị content hoặc sessionId tuỳ ý (ở đây lấy content)
        holder.txtChatTitle.setText(chatHistory.getContent());

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatHistoryList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtChatTitle;
        ImageView btnDelete;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtChatTitle = itemView.findViewById(R.id.txtChatTitle);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

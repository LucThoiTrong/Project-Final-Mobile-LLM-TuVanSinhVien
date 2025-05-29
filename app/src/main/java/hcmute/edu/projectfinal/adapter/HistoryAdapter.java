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
import hcmute.edu.projectfinal.model.ChatHistory;
import io.appwrite.exceptions.AppwriteException;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ChatViewHolder> {

    private final List<ChatHistory> chatHistoryList;

    // Interface để xử lý sự kiện click
    public interface OnClickListener {
        void onItemClick(View v, int position) throws AppwriteException;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_history, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatHistory chatHistory = chatHistoryList.get(position);

        // Hiển thị nội dung đại diện (ví dụ: content đầu tiên)
        holder.txtChatTitle.setText(chatHistory.getContent());

        // Sự kiện click vào item (để xem chi tiết)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                try {
                    listener.onItemClick(v, position);
                } catch (AppwriteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Sự kiện click nút xoá
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

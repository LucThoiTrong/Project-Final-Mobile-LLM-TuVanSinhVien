package hcmute.edu.projectfinal.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import hcmute.edu.projectfinal.R;
import hcmute.edu.projectfinal.adapter.MessageAdapter;
import hcmute.edu.projectfinal.model.ChatData;
import hcmute.edu.projectfinal.model.Message;
import hcmute.edu.projectfinal.service.AppWriteService;
import io.appwrite.exceptions.AppwriteException;

public class ChatFragment extends Fragment {
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private MessageAdapter messageAdapter;
    private AppWriteService appWriteService;

    // Cho hiệu ứng typing indicator
    private Handler typingAnimationHandler;
    private Runnable typingAnimationRunnable;
    private Message currentTypingMessage;
    private boolean isTypingAnimationRunning = false;
    private static final String BASE_TYPING_MESSAGE = "Đang phản hồi";
    private static final int TYPING_ANIMATION_INTERVAL = 600; // milliseconds

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appWriteService = AppWriteService.getInstance(requireContext());
        typingAnimationHandler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageEditText = view.findViewById(R.id.messageEditText);
        ImageButton sendButton = view.findViewById(R.id.sendButton);

        messageAdapter = new MessageAdapter(ChatData.messages);
        chatRecyclerView.setAdapter(messageAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupTypingAnimationRunnable();

        sendButton.setOnClickListener(v -> {
            String messageContent = messageEditText.getText().toString().trim();
            if (!messageContent.isEmpty()) {
                try {
                    handleUserMessage(messageContent);
                } catch (AppwriteException e) {
                    Log.e("AppwriteException", "Error handling user message: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                messageEditText.setText("");
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            String majorTitle = bundle.getString("major_title");
            if (majorTitle != null && !majorTitle.isEmpty()) {
                try {
                    handleUserMessage("Hãy tư vấn cho tui chuyên ngành " + majorTitle);
                } catch (AppwriteException e) {
                    Log.e("AppwriteException", "Error handling initial message: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        return view;
    }

    private void setupTypingAnimationRunnable() {
        typingAnimationRunnable = new Runnable() {
            private int dotState = 0; // 0 for ".", 1 for "..", 2 for "..."

            @Override
            public void run() {
                if (!isTypingAnimationRunning || currentTypingMessage == null || !ChatData.messages.contains(currentTypingMessage)) {
                    // Dừng nếu animation không còn chạy hoặc tin nhắn đã bị xóa
                    return;
                }

                dotState = (dotState + 1) % 3; // Chu kỳ 0, 1, 2
                String dots;
                if (dotState == 0) {
                    dots = ".";
                } else if (dotState == 1) {
                    dots = "..";
                } else {
                    dots = "...";
                }

                currentTypingMessage.setContent(BASE_TYPING_MESSAGE + dots);
                int messageIndex = ChatData.messages.indexOf(currentTypingMessage);
                if (messageIndex != -1) {
                    messageAdapter.notifyItemChanged(messageIndex);
                }
                // Lặp lại animation
                typingAnimationHandler.postDelayed(this, TYPING_ANIMATION_INTERVAL);
            }
        };
    }

    private void startTypingAnimation() {
        // Dừng animation cũ nếu đang chạy
        if (isTypingAnimationRunning) {
            stopTypingAnimation();
        }

        // Tạo và thêm tin nhắn "đang gõ..." mới
        currentTypingMessage = new Message(BASE_TYPING_MESSAGE + ".", "assistant");
        ChatData.messages.add(currentTypingMessage);
        int newMessageIndex = ChatData.messages.size() - 1;
        messageAdapter.notifyItemInserted(newMessageIndex);
        chatRecyclerView.scrollToPosition(newMessageIndex);

        isTypingAnimationRunning = true;
        // Bắt đầu runnable (sau khi xóa các callback cũ để đảm bảo)
        typingAnimationHandler.removeCallbacks(typingAnimationRunnable);
        typingAnimationHandler.postDelayed(typingAnimationRunnable, TYPING_ANIMATION_INTERVAL);
    }

    private void stopTypingAnimation() {
        if (!isTypingAnimationRunning && currentTypingMessage == null) {
            return; // Không có gì để dừng
        }

        isTypingAnimationRunning = false;
        typingAnimationHandler.removeCallbacks(typingAnimationRunnable);

        if (currentTypingMessage != null && ChatData.messages.contains(currentTypingMessage)) {
            int index = ChatData.messages.indexOf(currentTypingMessage);
            ChatData.messages.remove(index);
            messageAdapter.notifyItemRemoved(index);
        }
        currentTypingMessage = null;
    }

    private void handleUserMessage(String messageContent) throws AppwriteException {
        Message userMessage = new Message(messageContent, "user");
        ChatData.messages.add(userMessage);
        appWriteService.createDocument("user", messageContent); // Lưu tin nhắn người dùng
        messageAdapter.notifyItemInserted(ChatData.messages.size() - 1);
        chatRecyclerView.scrollToPosition(ChatData.messages.size() - 1);

        startTypingAnimation(); // Bắt đầu hiệu ứng "đang chờ"

        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("role", "user");
            jsonMessage.put("content", messageContent);
            ChatData.messagesJSONToSend.put(jsonMessage);
        } catch (JSONException e) {
            Log.e("JSON", "JSONException adding user message: " + e.getMessage());
        }

        try {
            callAzureOpenAI();
        } catch (JSONException e) {
            // Dừng animation nếu có lỗi ngay lúc gọi API
            stopTypingAnimation();
            Log.e("OpenAI Call", "JSONException during API call setup: " + e.getMessage());
            Toast.makeText(getContext(), "Lỗi khi chuẩn bị gửi tin nhắn", Toast.LENGTH_SHORT).show();
        }
    }

    private void callAzureOpenAI() throws JSONException {
        appWriteService.callAzureOpenAI(new AppWriteService.AppWriteCallback() {
            @Override
            public void onSuccess(Object result) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    stopTypingAnimation(); // Dừng và xóa tin nhắn "đang chờ"

                    if (result instanceof String) {
                        try {
                            JSONObject jsonResponse = new JSONObject((String) result);
                            String assistantReply = jsonResponse.getString("response");
                            String cleanedReply = normalizeReply(assistantReply);

                            try {
                                appWriteService.createDocument("assistant", cleanedReply);
                            } catch (AppwriteException e) {
                                Log.e("AppwriteError", "Failed to save assistant message: " + e.getMessage());
                            }

                            Message assistantMessage = new Message(cleanedReply, "assistant");
                            ChatData.messages.add(assistantMessage);
                            messageAdapter.notifyItemInserted(ChatData.messages.size() - 1);
                            chatRecyclerView.scrollToPosition(ChatData.messages.size() - 1);

                            JSONObject jsonAssistantMessage = new JSONObject();
                            jsonAssistantMessage.put("role", "assistant");
                            jsonAssistantMessage.put("content", cleanedReply);
                            ChatData.messagesJSONToSend.put(jsonAssistantMessage);

                        } catch (JSONException e) {
                            Log.e("JSON", "JSONException on success: " + e.getMessage());
                            Toast.makeText(getContext(), "Lỗi xử lý phản hồi từ server", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Định dạng phản hồi không mong muốn", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                if (getActivity() == null) return;
                requireActivity().runOnUiThread(() -> {
                    stopTypingAnimation(); // Dừng và xóa tin nhắn "đang chờ"
                    Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    if (!ChatData.messages.isEmpty()) {
                        chatRecyclerView.scrollToPosition(ChatData.messages.size() - 1);
                    }
                });
            }
        });
    }

    private String normalizeReply(String assistantReply) {
        return assistantReply
                .replaceAll("\\*\\*", "")
                .replaceAll("###", "")
                .replaceAll("\\\\/", "/")
                .replaceAll("^Raw:.*", "")
                .replaceAll("\\{\"role\".*", "")
                .trim();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Dừng animation khi fragment không còn hiển thị để tránh lỗi và tốn tài nguyên
        stopTypingAnimation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Dọn dẹp handler để tránh rò rỉ bộ nhớ
        if (typingAnimationHandler != null) {
            typingAnimationHandler.removeCallbacks(typingAnimationRunnable);
        }
        isTypingAnimationRunning = false;
        currentTypingMessage = null;
    }
}
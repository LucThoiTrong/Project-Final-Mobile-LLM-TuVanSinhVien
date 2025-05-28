package hcmute.edu.projectfinal.fragment;

import android.os.Bundle;
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

public class ChatFragment extends Fragment {

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private MessageAdapter messageAdapter;
    private AppWriteService appWriteService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appWriteService = AppWriteService.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Ánh xạ các thành phần trong layout
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageEditText = view.findViewById(R.id.messageEditText);
        ImageButton sendButton = view.findViewById(R.id.sendButton);

        // Khởi tạo MessageAdapter và thiết lập RecyclerView
        messageAdapter = new MessageAdapter(ChatData.messages);
        chatRecyclerView.setAdapter(messageAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Xử lý sự kiện nhấn nút gửi
        sendButton.setOnClickListener(v -> {
            String messageContent = messageEditText.getText().toString().trim();
            if (!messageContent.isEmpty()) {
                // Tạo tin nhắn người dùng
                Message userMessage = new Message();
                userMessage.setContent(messageContent);
                userMessage.setRole("user");

                // Thêm vào danh sách tin nhắn và cập nhật UI
                ChatData.messages.add(userMessage);
                messageAdapter.notifyItemInserted(ChatData.messages.size() - 1);
                chatRecyclerView.scrollToPosition(ChatData.messages.size() - 1);

                // Thêm vào JSON để gửi
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("role", "user");
                    jsonMessage.put("content", messageContent);
                    ChatData.messagesJSONToSend.put(jsonMessage);
                } catch (JSONException e) {
                    Log.e("JSON", "JSONException: " + e.getMessage());
                }

                // Gọi API Azure OpenAI
                try {
                    callAzureOpenAI();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                // Xóa nội dung EditText
                messageEditText.setText("");
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void callAzureOpenAI() throws JSONException {
        appWriteService.callAzureOpenAI(new AppWriteService.AppWriteCallback() {
            @Override
            public void onSuccess(Object result) {
                if (result instanceof String) {
                    try {
                        // Chuyển đổi chuỗi JSON kết quả thành JSONObject
                        JSONObject jsonResponse = new JSONObject((String) result);
                        // Lấy nội dung phản hồi của trợ lý ảo
                        String assistantReply = jsonResponse.getString("response"); // Giả sử key là "response" như trong ví dụ của bạn

                        // Tạo tin nhắn của trợ lý ảo
                        Message assistantMessage = new Message();
                        assistantMessage.setContent(assistantReply);
                        assistantMessage.setRole("assistant"); // Hoặc "bot", "system" tùy theo cách bạn định nghĩa

                        // Thêm vào danh sách tin nhắn và cập nhật UI
                        // Đảm bảo rằng việc cập nhật UI được thực hiện trên Main Thread
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                ChatData.messages.add(assistantMessage);
                                messageAdapter.notifyItemInserted(ChatData.messages.size() - 1);
                                chatRecyclerView.scrollToPosition(ChatData.messages.size() - 1);

                                JSONObject jsonAssistantMessage = new JSONObject();
                                try {
                                    jsonAssistantMessage.put("role", "assistant");
                                    jsonAssistantMessage.put("content", assistantReply);
                                    ChatData.messagesJSONToSend.put(jsonAssistantMessage);
                                } catch (JSONException e) {
                                    Log.e("JSON", "JSONException while adding assistant message: " + e.getMessage());
                                }
                            });
                        }

                    } catch (JSONException e) {
                        Log.e("JSON", "JSONException on success: " + e.getMessage());
                        // Xử lý lỗi parsing JSON ở đây, ví dụ hiển thị Toast
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Lỗi xử lý phản hồi từ server", Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }
}
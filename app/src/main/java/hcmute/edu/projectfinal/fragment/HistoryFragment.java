package hcmute.edu.projectfinal.fragment;

import static hcmute.edu.projectfinal.model.ChatData.chatHistory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import hcmute.edu.projectfinal.R;
import hcmute.edu.projectfinal.adapter.HistoryAdapter;
import hcmute.edu.projectfinal.model.ChatData;
import hcmute.edu.projectfinal.service.AppWriteService;
import io.appwrite.exceptions.AppwriteException;

public class HistoryFragment extends Fragment {

    private Button btnNewChat;
    private RecyclerView recyclerChatHistory;
    private HistoryAdapter chatAdapter;
    private AppWriteService appWriteService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appWriteService = AppWriteService.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initViews(view);
        setupRecyclerView();
        setupListeners();
        try {
            getChatHistory();
        } catch (AppwriteException e) {
            throw new RuntimeException(e);
        }
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getChatHistory() throws AppwriteException {
        appWriteService.getChatHistory(() ->
                requireActivity().runOnUiThread(() -> chatAdapter.notifyDataSetChanged()));
    }

    private void initViews(View view) {
        btnNewChat = view.findViewById(R.id.btnNewChat);
        recyclerChatHistory = view.findViewById(R.id.recyclerChatHistory);
    }

    private void setupRecyclerView() {
        recyclerChatHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new HistoryAdapter(chatHistory, new HistoryAdapter.OnClickListener() {
            // Sự kiện xem chi tiết history
            @Override
            public void onItemClick(View v, int position) throws AppwriteException {
                appWriteService.getDetailChatHistory(chatHistory.get(position).getSessionId(), () -> runChatHistory());
            }

            // Sự kiện xoá 1 history
            @Override
            public void onDeleteClick(View v, int position) {

            }
        });
        recyclerChatHistory.setAdapter(chatAdapter);
    }

    private void setupListeners() {
        btnNewChat.setOnClickListener(v -> startNewChat());
    }

    private void startNewChat() {
        ChatData.messages.clear();
        ChatData.messagesJSONToSend = new JSONArray();

        runChatHistory();
    }

    private void runChatHistory() {
        ChatTabFragment chatTabFragment = new ChatTabFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, chatTabFragment)
                .addToBackStack(null)
                .commit();
    }
}
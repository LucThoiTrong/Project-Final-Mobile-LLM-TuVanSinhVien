package hcmute.edu.projectfinal.service;

import static hcmute.edu.projectfinal.model.ChatData.chatHistory;
import static hcmute.edu.projectfinal.model.ChatData.messages;
import static hcmute.edu.projectfinal.model.ChatData.messagesJSONToSend;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import hcmute.edu.projectfinal.model.ChatHistory;
import hcmute.edu.projectfinal.model.Message;
import io.appwrite.Client;
import io.appwrite.ID;
import io.appwrite.Query;
import io.appwrite.coroutines.CoroutineCallback;
import io.appwrite.enums.ExecutionMethod;
import io.appwrite.enums.OAuthProvider;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import io.appwrite.services.Account;
import io.appwrite.services.Databases;
import io.appwrite.services.Functions;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

public class AppWriteService {
    private static AppWriteService instance;
    private final Account account;
    private final Functions functions;
    private final Databases databases;
    private final SharedPreferenceService sharedPreferenceService;
    private final String AppWrite = "appwrite";

    // Tạo 1 interface để thực hiện kết quả success hay fail
    public interface AppWriteCallback {
        void onSuccess(Object result);
        void onFailure(String error);
    }

    private AppWriteService(Context context) {
        Context applicationContext = context.getApplicationContext();
        String API_ENDPOINT = "https://appwrite-lts.duckdns.org/v1";
        String PROJECT_ID = "6827f062001c0e873d23";

        Client client = new Client(applicationContext, API_ENDPOINT)
                .setProject(PROJECT_ID)
                .setSelfSigned(true);

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        client.setHttp$library_release(new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .build());

        sharedPreferenceService = new SharedPreferenceService(applicationContext);

        functions = new Functions(client);
        account = new Account(client);
        databases = new Databases(client);
    }

    public static synchronized AppWriteService getInstance(Context context) {
        if(instance == null) {
            instance = new AppWriteService(context);
        }
        return instance;
    }

    // Đăng nhập
    public void createSession(AppCompatActivity activity, AppWriteCallback callback) {
        account.createOAuth2Session(
                activity,
                OAuthProvider.GOOGLE,
                new CoroutineCallback<>((result, error) -> {
                    if (error != null) {
                        Log.e(AppWrite, Objects.requireNonNull(error.getMessage()));
                        callback.onFailure(error.getMessage());
                    } else {
                        if(result != null) {
                            try {
                                getUserIdAndSessionId();
                            } catch (AppwriteException e) {
                                throw new RuntimeException(e);
                            }
                            Log.d(AppWrite, result.toString());
                            callback.onSuccess(result);
                        }
                    }
                })
        );
    }

    // Lấy thông tin tài khoản
    public void getUserIdAndSessionId() throws AppwriteException {
        // Lấy userId
        account.get(new CoroutineCallback<>((result, error) -> {
            if (error != null) {
                Log.e("Appwrite", Objects.requireNonNull(error.getMessage()));
            } else {
                if(result != null) {
                    String userId = result.getId();
                    sharedPreferenceService.put("userId", userId);
                    Log.d("Appwrite", result.toString());
                }
            }
        }));

        // Lấy sessionId
        account.getSession(
                "current", // sessionId
                new CoroutineCallback<>((result, error) -> {
                    if (error != null) {
                        Log.e("Appwrite", Objects.requireNonNull(error.getMessage()));
                    }
                    else if (result != null) {
                        String sessionId = result.getId();
                        sharedPreferenceService.put("sessionId", sessionId);
                        Log.d("Appwrite", result.toString());
                    }
                })
        );
    }
    public void getAccount(AppWriteCallback callback) throws AppwriteException {
        account.get(new CoroutineCallback<>((result, error) -> {
            if (error != null) {
                Log.e("Appwrite", Objects.requireNonNull(error.getMessage()));
                callback.onFailure(error.getMessage());
            } else {
                if(result != null) {
                    callback.onSuccess(result);
                    Log.d("Appwrite", result.toString());
                }
            }
        }));
    }

    // Đăng xuất
    public void logOut(AppWriteCallback callback) {
        account.deleteSessions(new CoroutineCallback<>((result, error) -> {
            if (error != null) {
                Log.e(AppWrite, Objects.requireNonNull(error.getMessage()));
                callback.onFailure(error.getMessage());
            } else {
                if (result != null) {
                    sharedPreferenceService.clear();
                    Log.d(AppWrite, result.toString());
                    callback.onSuccess(result);
                }
                Log.d(AppWrite, "Đăng xuất thành công");
            }
        }));
    }

    // Call Azure OpenAI
    public void callAzureOpenAI(AppWriteCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("messages", messagesJSONToSend);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        functions.createExecution(
                "6829e6b700149cf26fad",
                jsonBody.toString(),
                false, // nếu vẫn dùng false
                "/",
                ExecutionMethod.POST,
                new CoroutineCallback<>((result, error) -> {
                    if (error != null) {
                        Log.e("Appwrite", "Error: " + error.getMessage());
                        callback.onFailure(error.getMessage());
                    } else {
                        Log.d("Appwrite", Objects.requireNonNull(result).toString());
                        callback.onSuccess(result.getResponseBody());
                    }
                })
        );
    }

    // Create Document (Lưu 1 message của nguời dùng lên DB)
    public void createDocument(String role, String messageContent) throws AppwriteException {
        // Lấy userId
        String userId = sharedPreferenceService.get("userId");
        // Lấy sessionId
        String sessionId = sharedPreferenceService.get("sessionId");
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("sessionId", sessionId);
        data.put("role", role); // "user" hoặc "assistant"
        data.put("content", messageContent);

        databases.createDocument(
                "683796d0002a4e950d37", // databaseId
                "68379e6400202e59e779", // collectionId
                ID.Companion.unique(10),
                data,
                new CoroutineCallback<>((result, error) -> {
                    if (error != null) {
                        Log.e("Appwrite", Objects.requireNonNull(error.getMessage()));
                    } else {
                        if (result != null) {
                            Log.d("Appwrite", result.toString());
                        }

                    }
                })
        );
    }

    // Lấy Lịch sử trò chuyện
    public void getChatHistory(Runnable runnable) throws AppwriteException {
        String userId = sharedPreferenceService.get("userId");
        databases.listDocuments(
                "683796d0002a4e950d37", // databaseId
                "68379e6400202e59e779", // collectionId
                List.of(Query.Companion.equal("userId", userId)),
                new CoroutineCallback<>((result, error) -> {
                    if (error != null) {
                        Log.e("Appwrite", Objects.requireNonNull(error.getMessage()));
                        return;
                    }
                    if (result != null) {
                        if (!result.getDocuments().isEmpty()) {
                            // Lọc các session và content (chỉ lấy 1 thằng đại diện)
                            Map<String, String> uniqueSessions = extractUniqueSessions(result.getDocuments());

                            // Xoá lịch sử cũ
                            chatHistory.clear();

                            // Thực hiện lưu trữ vô mảng ChatHistory => show lên UI
                            for (Map.Entry<String, String> entry : uniqueSessions.entrySet()) {

                                String sessionId = entry.getKey();
                                String content = entry.getValue();

                                Log.d("Appwrite", "Session ID: " + sessionId);
                                Log.d("Appwrite", "Content: " + content);

                                ChatHistory chat = new ChatHistory(sessionId, content);

                                chatHistory.add(chat);
                                if(runnable != null) {
                                    runnable.run();
                                }
                            }
                        }
                    }
                })
        );
    }

    public Map<String, String> extractUniqueSessions(List<Document<Map<String, Object>>> documents) {
        Map<String, String> uniqueSessions = new LinkedHashMap<>(); // giữ thứ tự, tránh trùng
        documents.stream().map(Document::getData).forEach(data -> {
            String sessionId = (String) data.get("sessionId");
            String content = (String) data.get("content");
            if (!uniqueSessions.containsKey(sessionId)) {
                uniqueSessions.put(sessionId, content);
            }
        });
        return uniqueSessions;
    }


    // Lấy chi tiết lịch sử của 1 lần trò chuyện
    public void getDetailChatHistory(String sessionId, Runnable runnable) throws AppwriteException {
        databases.listDocuments(
                "683796d0002a4e950d37", // databaseId
                "68379e6400202e59e779", // collectionId
                List.of(Query.Companion.equal("sessionId", sessionId)),
                new CoroutineCallback<>((result, error) -> {
                    if (error != null) {
                        Log.e("Appwrite", Objects.requireNonNull(error.getMessage()));
                        return;
                    }

                    if (result != null && !result.getDocuments().isEmpty()) {
                        // Xoá dữ liệu cuộc trò chuyện hiện tại
                        messages.clear();
                        messagesJSONToSend = new JSONArray();

                        // Đồng thời set up lại phiên làm việc trong sharedPreference
                        sharedPreferenceService.put("sessionId", sessionId);

                        // Tiến hành add lại dữ liệu mới
                        result.getDocuments().forEach(doc -> {
                            String role = Objects.requireNonNull(doc.getData().get("role")).toString();
                            String content = Objects.requireNonNull(doc.getData().get("content")).toString();

                            Log.d("ChiTiet", role + ": " + content);

                            // add các message show lên UI
                            Message message = new Message(content, role);
                            messages.add(message);

                            // add các message để gửi cho Appwrite Function
                            JSONObject jsonMessage = new JSONObject();
                            try {
                                jsonMessage.put("role", role);
                                jsonMessage.put("content", content);
                                messagesJSONToSend.put(jsonMessage);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });

                        // Gọi lại runnable để cập nhật UI
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                })
        );
    }

    // Thực hiện xoá 1 lịch sử trò chuyện
    public void deleteConversationBySessionId(String sessionId, Runnable runnable) throws AppwriteException {
        // Kiểm tra trường hợp xoá là xoá ngay cuộc trò chuyện đang chat
        if(sessionId.equals(sharedPreferenceService.get("sessionId"))) {
            refeshConversation();
        }

        databases.listDocuments(
                "683796d0002a4e950d37", // databaseId
                "68379e6400202e59e779", // collectionId
                List.of(Query.Companion.equal("sessionId", sessionId)),
                new CoroutineCallback<>((result, error) -> {
                    if (error != null) {
                        Log.e("Appwrite", "Lỗi khi truy vấn session: " + error.getMessage());
                        return;
                    }

                    if (result != null && !result.getDocuments().isEmpty()) {
                        List<Document<Map<String, Object>>> documents = result.getDocuments();
                        int total = documents.size();
                        int[] counter = {0};

                        documents.stream()
                                .map(Document::getId)
                                .forEach(documentId -> databases.deleteDocument(
                                "683796d0002a4e950d37", // databaseId
                                "68379e6400202e59e779", // collectionId
                                documentId,
                                new CoroutineCallback<>((deleteResult, deleteError) -> {
                                    synchronized (counter) {
                                        counter[0]++;
                                        if (deleteError != null) {
                                            Log.e("Appwrite", "Lỗi xoá document: " + deleteError.getMessage());
                                        } else {
                                            Log.d("Appwrite", "Đã xoá document: " + documentId);
                                        }

                                        // Gọi runnable khi đã xoá hết
                                        if (counter[0] == total && runnable != null) {
                                            runnable.run();
                                        }
                                    }
                                })
                        ));
                    } else {
                        Log.d("Appwrite", "Không tìm thấy document nào với sessionId: " + sessionId);
                    }
                })
        );
    }

    public void refeshConversation() {
        // Khởi tạo lại dữ liệu cuộc hội thoại
        messages.clear();
        messagesJSONToSend = new JSONArray();
        // Tạo mới session ID
        sharedPreferenceService.put("sessionId", ID.Companion.unique(20));
    }

}
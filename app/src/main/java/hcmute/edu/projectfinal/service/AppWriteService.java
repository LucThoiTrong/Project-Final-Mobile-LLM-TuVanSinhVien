package hcmute.edu.projectfinal.service;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import hcmute.edu.projectfinal.model.ChatData;
import io.appwrite.Client;
import io.appwrite.coroutines.CoroutineCallback;
import io.appwrite.enums.ExecutionMethod;
import io.appwrite.enums.OAuthProvider;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Account;
import io.appwrite.services.Functions;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

public class AppWriteService {
    private static AppWriteService instance;
    private final Account account;
    private final Functions functions;
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

        functions = new Functions(client);
        account = new Account(client);
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
                            Log.d(AppWrite, result.toString());
                            callback.onSuccess(result);
                        }
                    }
                })
        );
    }

    // Lấy thông tin tài khoản
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
            jsonBody.put("messages", ChatData.messagesJSONToSend);
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
}
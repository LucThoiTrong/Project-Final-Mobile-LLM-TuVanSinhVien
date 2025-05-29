package hcmute.edu.projectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;

import java.util.ArrayList;

import hcmute.edu.projectfinal.model.ChatData;
import hcmute.edu.projectfinal.service.AppWriteService;

public class MainActivity extends AppCompatActivity {
    private AppWriteService appWriteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        appWriteService = AppWriteService.getInstance(this);
        Button googleSignInButton = findViewById(R.id.btnLogin);
        googleSignInButton.setOnClickListener(v -> {
            // Thực hiện đăng nhập bằng Google
            appWriteService.createSession(MainActivity.this, new AppWriteService.AppWriteCallback() {
                @Override
                public void onSuccess(Object result) {
                    // Khởi tạo các mảng lưu trữ
                    ChatData.messagesJSONToSend = new JSONArray();
                    ChatData.messages = new ArrayList<>();
                    ChatData.chatHistory = new ArrayList<>();

                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show());
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show());                }
            });
        });
    }
}
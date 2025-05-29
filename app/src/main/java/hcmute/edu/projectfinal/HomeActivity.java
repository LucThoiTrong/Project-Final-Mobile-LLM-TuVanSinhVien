package hcmute.edu.projectfinal;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import hcmute.edu.projectfinal.fragment.AccountFragment;
import hcmute.edu.projectfinal.fragment.ChatTabFragment;
import hcmute.edu.projectfinal.fragment.MajorFragment;
import hcmute.edu.projectfinal.fragment.TestFragment;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        // Xử lý khoảng cách cho các khu vực hệ thống (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // Thêm padding
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_major) {
                selectedFragment = new MajorFragment();
            } else if (item.getItemId() == R.id.nav_account) {
                selectedFragment = new AccountFragment();
            } else if (item.getItemId() == R.id.nav_chat) {
                selectedFragment = new ChatTabFragment();
            } else if (item.getItemId() == R.id.nav_test) {
                selectedFragment = new TestFragment();
            }

            // Replace the fragment in the container
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Set default fragment (MajorFragment)
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_major);
        }
    }
}
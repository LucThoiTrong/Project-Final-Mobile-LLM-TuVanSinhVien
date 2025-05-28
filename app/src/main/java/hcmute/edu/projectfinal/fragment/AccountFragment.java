package hcmute.edu.projectfinal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import hcmute.edu.projectfinal.MainActivity;
import hcmute.edu.projectfinal.R;
import hcmute.edu.projectfinal.service.AppWriteService;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.User;

public class AccountFragment extends Fragment {
    private TextView tvName, tvEmail;
    private AppWriteService appWriteService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Khởi tạo các view
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        appWriteService = AppWriteService.getInstance(requireContext());
        try {
            appWriteService.getAccount(new AppWriteService.AppWriteCallback() {
                @Override
                public void onSuccess() {}
    
                @Override
                public void onSuccess(Object result) {
                    if (result instanceof User) {
                        requireActivity().runOnUiThread(() -> {
                            tvName.setText(((User<?>) result).getName());
                            tvEmail.setText(((User<?>) result).getEmail());
                        });
                    }
                }
    
                @Override
                public void onFailure(String error) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(),"Lỗi đăng nhập: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (AppwriteException e) {
            throw new RuntimeException(e);
        }

        // Xử lý nút đăng xuất
        btnLogout.setOnClickListener(v -> {
            // TODO: Thêm logic đăng xuất (ví dụ: gọi account.deleteSession())
            Log.d("Logout", "Nút đăng xuất được nhấn");
            appWriteService.logOut(new AppWriteService.AppWriteCallback() {

                @Override
                public void onSuccess() {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                        requireActivity().startActivity(new Intent(requireContext(), MainActivity.class));
                        requireActivity().finish();
                    });
                }

                @Override
                public void onSuccess(Object result) {

                }

                @Override
                public void onFailure(String error) {

                }
            });
        });

        return view;
    }
}
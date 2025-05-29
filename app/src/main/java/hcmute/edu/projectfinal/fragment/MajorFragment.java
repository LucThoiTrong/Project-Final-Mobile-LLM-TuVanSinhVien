package hcmute.edu.projectfinal.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import hcmute.edu.projectfinal.HomeActivity;
import hcmute.edu.projectfinal.R;

public class MajorFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_major, container, false);

        // Ánh xạ các CardView từ layout
        CardView cardSoftwareEngineering = view.findViewById(R.id.card_software_engineering);
        CardView cardArtificialIntelligence = view.findViewById(R.id.card_artificial_intelligence);
        CardView cardNetworkSecurity = view.findViewById(R.id.card_network_security);
        CardView cardInformationSystems = view.findViewById(R.id.card_information_systems);

        // Thiết lập sự kiện click cho các CardView
        cardSoftwareEngineering.setOnClickListener(v -> showMajorDialog("Công nghệ Phần mềm",
                "Đào tạo các kỹ năng cần thiết để phát triển, thiết kế, kiểm thử và quản lý các sản phẩm phần mềm đáp ứng nhu cầu thực tiễn.",
                "Nhập môn lập trình, Cấu trúc dữ liệu, Kỹ thuật phần mềm, Lập trình Web, Kiểm thử phần mềm",
                "12-25 triệu VNĐ/tháng",
                "Kỹ sư phần mềm, Lập trình viên Full-stack, Kỹ sư kiểm thử, Kiến trúc sư phần mềm",
                R.drawable.img_cnpm));

        cardArtificialIntelligence.setOnClickListener(v -> showMajorDialog("Trí tuệ Nhân tạo (AI)",
                "Đào tạo kiến thức và kỹ năng để phát triển các hệ thống thông minh, ứng dụng học máy, xử lý ngôn ngữ tự nhiên và thị giác máy tính.",
                "Học máy, Học sâu, Xử lý ngôn ngữ tự nhiên, Thị giác máy tính, Khoa học dữ liệu",
                "15-30 triệu VNĐ/tháng",
                "Kỹ sư AI, Nhà khoa học dữ liệu, Kỹ sư NLP, Kỹ sư thị giác máy tính",
                R.drawable.img_ttnt));

        cardNetworkSecurity.setOnClickListener(v -> showMajorDialog("Mạng Máy tính và An ninh mạng",
                "Đào tạo kiến thức và kỹ năng về thiết kế, xây dựng, vận hành, quản trị và bảo mật các hệ thống mạng máy tính.",
                "Nhập môn Mạng máy tính, Giao thức TCP/IP, An ninh mạng, Mật mã học, Phòng chống tấn công mạng",
                "12-22 triệu VNĐ/tháng",
                "Kỹ sư mạng, Chuyên viên an ninh mạng, Kỹ sư bảo mật, Chuyên gia kiểm thử xâm nhập",
                R.drawable.img_anm));

        cardInformationSystems.setOnClickListener(v -> showMajorDialog("Hệ thống Thông tin",
                "Tập trung vào việc phân tích, thiết kế, phát triển, vận hành và quản lý các hệ thống thông tin để hỗ trợ doanh nghiệp.",
                "Phân tích hệ thống, Cơ sở dữ liệu, ERP, Thương mại điện tử, Kinh doanh thông minh",
                "10-20 triệu VNĐ/tháng",
                "Chuyên viên phân tích nghiệp vụ, Quản trị cơ sở dữ liệu, Quản lý dự án IT, Chuyên viên BI",
                R.drawable.img_httt));

        return view;
    }

    // Hàm hiển thị dialog với thông tin chuyên ngành
    private void showMajorDialog(String title, String description, String subjects, String salary, String jobs, int idImage) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.fragment_dialog_major);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Ánh xạ các thành phần trong dialog
        ImageView imgLogo = dialog.findViewById(R.id.img_logo);
        TextView tvTitle = dialog.findViewById(R.id.tv_major_title);
        TextView tvDescription = dialog.findViewById(R.id.tv_description);
        TextView tvSubjects = dialog.findViewById(R.id.tv_subjects);
        TextView tvSalary = dialog.findViewById(R.id.tv_salary);
        TextView tvJobs = dialog.findViewById(R.id.tv_jobs);
        Button btnClose = dialog.findViewById(R.id.btn_close);
        Button btnConsult = dialog.findViewById(R.id.btn_consult);

        // Gán dữ liệu cho các TextView
        tvTitle.setText(title);
        tvDescription.setText(description);
        tvSubjects.setText(subjects);
        tvSalary.setText(salary);
        tvJobs.setText(jobs);
        imgLogo.setImageResource(idImage);

        // Xử lý sự kiện cho nút Đóng
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Xử lý sự kiện cho nút Nhận tư vấn (mở giao diện chatbot)
        btnConsult.setOnClickListener(v -> {
            ChatTabFragment chatTabFragment = new ChatTabFragment();

            // Cập nhật trạng thái focus của BottomNavigationView
            if (requireActivity() instanceof HomeActivity) {
                ((HomeActivity) requireActivity()).updateBottomNavigationFocus(R.id.nav_chat);
            }

            Bundle bundle = new Bundle();
            bundle.putString("major_title", title); // Truyền title
            bundle.putInt("target_tab_index", 0);   // Truyền index tab 'Trò chuyện'
            chatTabFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, chatTabFragment)
                    .addToBackStack(null)
                    .commit();
            dialog.dismiss();
        });
        dialog.show();
    }
}
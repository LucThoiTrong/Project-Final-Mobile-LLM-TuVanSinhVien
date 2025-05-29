package hcmute.edu.projectfinal.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.*;

import hcmute.edu.projectfinal.HomeActivity;
import hcmute.edu.projectfinal.R;
import hcmute.edu.projectfinal.model.ChatData;
import hcmute.edu.projectfinal.model.Question;

public class TestFragment extends Fragment {

    private ViewGroup fragmentContainer;
    private View introductionView;

    private TextView tvQuestionNumber, tvQuestionText;
    private RadioGroup rgOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnNextQuestion;
    private Button btnRequestConsultation; // Nút mới cho tư vấn

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    /** @noinspection MismatchedQueryAndUpdateOfCollection*/
    private final Map<Integer, Integer> userAnswers = new HashMap<>();
    private final Map<String, Integer> specializationScores = new HashMap<>();

    private final String[] specializations = {
            "Công nghệ Phần mềm",
            "Hệ thống Thông tin",
            "Trí tuệ Nhân tạo",
            "Mạng và An ninh mạng"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentContainer = (ViewGroup) inflater.inflate(R.layout.fragment_test_question, container, false);
        introductionView = inflater.inflate(R.layout.fragment_test_introduction, fragmentContainer, false);
        showIntroductionScreen();
        return fragmentContainer;
    }

    private void showIntroductionScreen() {
        fragmentContainer.removeAllViews();
        fragmentContainer.addView(introductionView);

        Button btnStartTest = introductionView.findViewById(R.id.btnStartTest);
        btnStartTest.setOnClickListener(v -> startTest());
    }

    private void startTest() {
        inflateQuestionView();
        resetTestState();
        showQuestion();
    }

    private void inflateQuestionView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View questionView = inflater.inflate(R.layout.fragment_test_question, fragmentContainer, false);

        tvQuestionNumber = questionView.findViewById(R.id.tvQuestionNumber);
        tvQuestionText = questionView.findViewById(R.id.tvQuestionText);
        rgOptions = questionView.findViewById(R.id.rgOptions);
        rbOption1 = questionView.findViewById(R.id.rbOption1);
        rbOption2 = questionView.findViewById(R.id.rbOption2);
        rbOption3 = questionView.findViewById(R.id.rbOption3);
        rbOption4 = questionView.findViewById(R.id.rbOption4);
        btnNextQuestion = questionView.findViewById(R.id.btnNextQuestion);
        btnRequestConsultation = questionView.findViewById(R.id.btnRequestConsultation);

        btnNextQuestion.setOnClickListener(v -> handleNextClick());
        if (btnRequestConsultation != null) {
            btnRequestConsultation.setVisibility(View.GONE);
        }
        fragmentContainer.removeAllViews();
        fragmentContainer.addView(questionView);
    }

    private void resetTestState() {
        questions = ChatData.specializationQuestions;
        currentQuestionIndex = 0;
        userAnswers.clear();
        specializationScores.clear();
        for (String spec : specializations) {
            specializationScores.put(spec, 0);
        }
    }

    private void handleNextClick() {
        int selectedId = rgOptions.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(getContext(), "Vui lòng chọn một đáp án", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedIndex = rgOptions.indexOfChild(rgOptions.findViewById(selectedId));
        userAnswers.put(currentQuestionIndex, selectedIndex);

        if (selectedIndex >= 0 && selectedIndex < specializations.length) {
            String spec = specializations[selectedIndex];
            //noinspection DataFlowIssue
            specializationScores.put(spec, specializationScores.getOrDefault(spec, 0) + 1);
        }

        if (++currentQuestionIndex < questions.size()) {
            showQuestion();
        } else {
            showResult();
        }
    }

    @SuppressLint("DefaultLocale")
    private void showQuestion() {
        Question q = questions.get(currentQuestionIndex);
        tvQuestionNumber.setText(String.format("Câu hỏi %d/%d", currentQuestionIndex + 1, questions.size()));
        tvQuestionText.setText(q.getQuestionText());

        List<String> opts = q.getOptions();
        rbOption1.setText(opts.get(0));
        rbOption2.setText(opts.get(1));
        rbOption3.setText(opts.get(2));
        rbOption4.setText(opts.get(3));
        rgOptions.clearCheck();

        btnNextQuestion.setText(currentQuestionIndex == questions.size() - 1 ? "Hoàn thành" : "Câu tiếp theo");
        if (btnRequestConsultation != null) {
            btnRequestConsultation.setVisibility(View.GONE);
        }
    }

    private String buildResultMessageString(List<String> topSpecs) {
        StringBuilder messageBuilder = new StringBuilder("Bài test hoàn thành!\n");
        if (topSpecs.isEmpty()) {
            messageBuilder.append("Không có đủ thông tin để đưa ra gợi ý. Bạn hãy thử làm lại bài test nhé!");
        } else if (topSpecs.size() == 1) {
            messageBuilder.append("Chuyên ngành phù hợp nhất có thể là:\n- ").append(topSpecs.get(0))
                    .append("\n\nHãy tìm hiểu thêm thông tin chi tiết về chuyên ngành này nhé!");
        } else {
            messageBuilder.append("Bạn phù hợp với nhiều chuyên ngành:\n");
            for (String spec : topSpecs) {
                messageBuilder.append("- ").append(spec).append("\n");
            }
            messageBuilder.append("\nBạn có thể tìm hiểu thêm để đưa ra quyết định cuối cùng.");
        }
        return messageBuilder.toString();
    }

    @SuppressLint("SetTextI18n")
    private void showResult() {
        final List<String> topSpecs = getTopSpecializations();
        String resultMessageString = buildResultMessageString(topSpecs);

        tvQuestionText.setText(resultMessageString);
        tvQuestionNumber.setVisibility(View.GONE);
        rgOptions.setVisibility(View.GONE);

        btnNextQuestion.setText("Làm lại Test");
        btnNextQuestion.setVisibility(View.VISIBLE);
        btnNextQuestion.setOnClickListener(v -> showIntroductionScreen());

        if (btnRequestConsultation != null) {
            btnRequestConsultation.setText("Nhận tư vấn ngay");
            btnRequestConsultation.setVisibility(View.VISIBLE);
            btnRequestConsultation.setOnClickListener(v -> {
                ChatFragment chatFragment = new ChatFragment();

                // Cập nhật trạng thái focus của BottomNavigationView
                if (requireActivity() instanceof HomeActivity) {
                    ((HomeActivity) requireActivity()).updateBottomNavigationFocus(R.id.nav_chat);
                }

                Bundle bundle = new Bundle();
                String majorTitleForChat = getString(topSpecs);

                bundle.putString("major_title", majorTitleForChat);
                chatFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, chatFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }
    }

    private static String getString(List<String> topSpecs) {
        String majorTitleForChat;

        if (topSpecs.isEmpty()) {
            // Trường hợp không có chuyên ngành nào nổi bật, hoặc muốn một tiêu đề chung
            majorTitleForChat = "Tư vấn chung về chuyên ngành";
        } else if (topSpecs.size() == 1) {
            // Một chuyên ngành phù hợp nhất
            majorTitleForChat = topSpecs.get(0);
        } else {
            // Nhiều chuyên ngành phù hợp, nối chúng lại
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < topSpecs.size(); i++) {
                sb.append(topSpecs.get(i));
                if (i < topSpecs.size() - 1) {
                    sb.append(", "); // Phân tách bằng dấu phẩy và khoảng trắng
                }
            }
            majorTitleForChat = sb.toString();
        }
        return majorTitleForChat;
    }

    private List<String> getTopSpecializations() {
        int maxScore = 0;
        // Kiểm tra xem specializationScores có rỗng không trước khi tìm max
        if (!specializationScores.isEmpty()) {
            Collection<Integer> scores = specializationScores.values();
            if (!scores.isEmpty()) { // Đảm bảo collection scores không rỗng
                maxScore = Collections.max(scores);
            }
        }

        if (maxScore == 0) return Collections.emptyList();

        List<String> top = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : specializationScores.entrySet()) {
            if (entry.getValue() == maxScore) {
                top.add(entry.getKey());
            }
        }
        return top;
    }
}
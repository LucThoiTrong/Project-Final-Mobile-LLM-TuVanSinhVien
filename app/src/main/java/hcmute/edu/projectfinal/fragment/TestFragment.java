package hcmute.edu.projectfinal.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color; // Import Color
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart; // Import PieChart
import com.github.mikephil.charting.components.Legend; // Import Legend for styling
import com.github.mikephil.charting.data.PieData; // Import PieData
import com.github.mikephil.charting.data.PieDataSet; // Import PieDataSet
import com.github.mikephil.charting.data.PieEntry; // Import PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter; // Import PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate; // Import ColorTemplate

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
    private Button btnRequestConsultation;
    private PieChart pieChartResult; // Declare PieChart

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
        fragmentContainer = (ViewGroup) inflater.inflate(R.layout.fragment_test_question, container, false); // Inflate the default layout first
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
        inflateQuestionView(); // This will inflate the question layout and find the PieChart
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
        pieChartResult = questionView.findViewById(R.id.pieChartResult); // Initialize PieChart

        btnNextQuestion.setOnClickListener(v -> handleNextClick());
        if (btnRequestConsultation != null) {
            btnRequestConsultation.setVisibility(View.GONE);
        }
        if (pieChartResult != null) { // Hide pie chart initially
            pieChartResult.setVisibility(View.GONE);
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
        if (pieChartResult != null) {
            pieChartResult.setVisibility(View.GONE); // Ensure chart is hidden on reset
        }
        if (tvQuestionNumber != null) {
            tvQuestionNumber.setVisibility(View.VISIBLE);
        }
        if (rgOptions != null) {
            rgOptions.setVisibility(View.VISIBLE);
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
        tvQuestionNumber.setVisibility(View.VISIBLE);
        rgOptions.setVisibility(View.VISIBLE);

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
        if (pieChartResult != null) { // Ensure pie chart is hidden during questions
            pieChartResult.setVisibility(View.GONE);
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

        // Setup and display PieChart
        setupPieChart();
        loadPieChartData();
        if (pieChartResult != null) {
            pieChartResult.setVisibility(View.VISIBLE);
        }


        btnNextQuestion.setText("Làm lại Test");
        btnNextQuestion.setVisibility(View.VISIBLE);
        btnNextQuestion.setOnClickListener(v -> showIntroductionScreen());

        if (btnRequestConsultation != null) {
            btnRequestConsultation.setText("Nhận tư vấn ngay");
            btnRequestConsultation.setVisibility(View.VISIBLE);
            btnRequestConsultation.setOnClickListener(v -> {
                ChatFragment chatFragment = new ChatFragment();

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

    private void setupPieChart() {
        if (pieChartResult == null) return;

        pieChartResult.setDrawHoleEnabled(true); // Draw a hole in the middle
        pieChartResult.setUsePercentValues(true); //
        // pieChartResult.setEntryLabelTextSize(12f); // Tắt dòng này hoặc setDrawEntryLabels(false)
        pieChartResult.setDrawEntryLabels(false); // KHÔNG hiển thị tên chuyên ngành trên lát cắt
        // pieChartResult.setEntryLabelColor(Color.BLACK); // Không cần nếu không vẽ entry label

        pieChartResult.setCenterText("Chuyên ngành"); //
        pieChartResult.setCenterTextSize(18f); //
        pieChartResult.getDescription().setEnabled(false); //

        Legend l = pieChartResult.getLegend(); //
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // Đưa xuống dưới
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // Căn giữa theo chiều ngang
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Hướng ngang cho các mục chú thích
        l.setDrawInside(false); // Vẽ bên ngoài biểu đồ
        l.setWordWrapEnabled(true); // Cho phép tự xuống dòng nếu tên chú thích dài
        l.setEnabled(true); //
    }

    private void loadPieChartData() {
        if (pieChartResult == null) return;

        ArrayList<PieEntry> entries = new ArrayList<>();
        boolean hasData = false;
        for (String specialization : specializations) {
            //noinspection DataFlowIssue
            int score = specializationScores.getOrDefault(specialization, 0);
            if (score > 0) {
                // Tên chuyên ngành (specialization) vẫn được dùng cho Legend
                entries.add(new PieEntry(score, specialization)); //
                hasData = true;
            }
        }

        if (!hasData) {
            pieChartResult.setVisibility(View.GONE);
            return;
        }

        PieData data = getPieData(entries); //

        pieChartResult.setData(data); //
        pieChartResult.invalidate(); // Refresh the chart
        pieChartResult.animateY(1400); // Add a little animation
    }

    @NonNull
    private PieData getPieData(ArrayList<PieEntry> entries) {
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) { //
            colors.add(color);
        }
        for (int color : ColorTemplate.VORDIPLOM_COLORS) { //
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, ""); // Label for the dataset (can be empty)
        dataSet.setColors(colors); //

        // Quan trọng: Chỉ hiển thị giá trị (%), không hiển thị label trên slice
        dataSet.setDrawValues(true); // Hiển thị giá trị (phần trăm)
        // dataSet.setDrawEntryLabels(false); // Đã set ở setupPieChart chung cho cả chart

        PieData data = new PieData(dataSet); //
        data.setValueFormatter(new PercentFormatter(pieChartResult)); // Show percentage on slices
        data.setValueTextSize(12f); //
        data.setValueTextColor(Color.BLACK); //
        return data;
    }


    private static String getString(List<String> topSpecs) {
        String majorTitleForChat;

        if (topSpecs.isEmpty()) {
            majorTitleForChat = "Tư vấn chung về chuyên ngành";
        } else if (topSpecs.size() == 1) {
            majorTitleForChat = topSpecs.get(0);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < topSpecs.size(); i++) {
                sb.append(topSpecs.get(i));
                if (i < topSpecs.size() - 1) {
                    sb.append(", ");
                }
            }
            majorTitleForChat = sb.toString();
        }
        return majorTitleForChat;
    }

    private List<String> getTopSpecializations() {
        int maxScore = 0;
        if (!specializationScores.isEmpty()) {
            Collection<Integer> scores = specializationScores.values();
            if (!scores.isEmpty()) {
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
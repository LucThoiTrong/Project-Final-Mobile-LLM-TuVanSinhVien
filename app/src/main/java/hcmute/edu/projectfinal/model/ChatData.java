package hcmute.edu.projectfinal.model;

import org.json.JSONArray; //
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatData {
    public static List<Message> messages; // dùng để hiển thị câu hỏi và câu trả lời
    public static JSONArray messagesJSONToSend; // dùng để gửi dữ liệu lên appwrite function (gồm câu hỏi và câu trả lời)
    public static List<ChatHistory> chatHistory;
    public static final List<Question> specializationQuestions = new ArrayList<>();

    static {
        // Câu 1
        specializationQuestions.add(new Question(
                "Bạn thích làm việc với loại công việc nào nhất?",
                Arrays.asList(
                        "A. Xây dựng ứng dụng hoặc website mà người dùng có thể tương tác trực tiếp.", // CNPM
                        "B. Phân tích dữ liệu để hỗ trợ doanh nghiệp đưa ra quyết định.", // HTTT
                        "C. Phát triển các hệ thống thông minh, như chatbot hoặc nhận diện hình ảnh.", // AI
                        "D. Thiết kế và vận hành hệ thống mạng hoặc máy chủ." // M&ANM
                )
        ));

        // Câu 2
        specializationQuestions.add(new Question(
                "Bạn đã từng hoặc muốn thử sức với ngôn ngữ lập trình nào dưới đây?",
                Arrays.asList(
                        "A. JavaScript, Kotlin, hoặc Flutter để làm web/mobile.", // CNPM
                        "B. SQL hoặc Python để xử lý cơ sở dữ liệu.", // HTTT
                        "C. Python với TensorFlow hoặc PyTorch để xây dựng mô hình AI.", // AI
                        "D. Python hoặc C cho lập trình mạng hoặc hệ thống." // M&ANM
                )
        ));

        // Câu 3
        specializationQuestions.add(new Question(
                "Khi làm việc nhóm, bạn muốn đảm nhận vai trò nào?",
                Arrays.asList(
                        "A. Lập trình viên chính, viết code cho sản phẩm.", // CNPM
                        "B. Phân tích yêu cầu và thiết kế hệ thống.", // HTTT
                        "C. Nghiên cứu và phát triển thuật toán mới.", // AI
                        "D. Quản lý cơ sở hạ tầng mạng hoặc bảo mật." // M&ANM
                )
        ));

        // Câu 4
        specializationQuestions.add(new Question(
                "Bạn cảm thấy hứng thú nhất với loại dự án nào?",
                Arrays.asList(
                        "A. Phát triển một ứng dụng mobile hoặc website thương mại điện tử.", // CNPM
                        "B. Xây dựng hệ thống quản lý doanh nghiệp (ERP) hoặc báo cáo dữ liệu.", // HTTT
                        "C. Tạo một hệ thống nhận diện giọng nói hoặc phân tích dữ liệu lớn.", // AI
                        "D. Thiết lập mạng nội bộ hoặc hệ thống đám mây cho công ty." // M&ANM
                )
        ));

        // Câu 5
        specializationQuestions.add(new Question(
                "Bạn thích môi trường làm việc nào hơn?",
                Arrays.asList(
                        "A. Làm việc với đội phát triển phần mềm, liên tục cập nhật sản phẩm.", // CNPM
                        "B. Làm việc với các phòng ban doanh nghiệp để tối ưu quy trình.", // HTTT
                        "C. Làm việc trong phòng nghiên cứu hoặc startup công nghệ cao.", // AI
                        "D. Quản lý và giám sát hệ thống mạng hoặc máy chủ." // M&ANM
                )
        ));

        // Câu 6
        specializationQuestions.add(new Question(
                "Bạn có kỹ năng hoặc hứng thú với công cụ nào dưới đây?",
                Arrays.asList(
                        "A. React, Android Studio, hoặc Selenium.", // CNPM
                        "B. Power BI, Tableau, hoặc SAP.", // HTTT
                        "C. TensorFlow, PyTorch, hoặc OpenCV.", // AI
                        "D. Cisco Packet Tracer, AWS, hoặc Linux." // M&ANM
                )
        ));

        // Câu 7
        specializationQuestions.add(new Question(
                "Bạn muốn học sâu về lĩnh vực nào?",
                Arrays.asList(
                        "A. Kỹ thuật phần mềm và kiểm thử phần mềm.", // CNPM
                        "B. Phân tích dữ liệu và hệ thống thông tin doanh nghiệp.", // HTTT
                        "C. Học máy và xử lý ngôn ngữ tự nhiên.", // AI
                        "D. Giao thức mạng và bảo mật hệ thống." // M&ANM
                )
        ));

        // Câu 8
        specializationQuestions.add(new Question(
                "Bạn cảm thấy mình giỏi ở kỹ năng nào?",
                Arrays.asList(
                        "A. Viết code nhanh và tạo giao diện người dùng đẹp.", // CNPM
                        "B. Phân tích số liệu và trình bày báo cáo.", // HTTT
                        "C. Tư duy logic và giải quyết bài toán phức tạp.", // AI
                        "D. Cấu hình thiết bị mạng hoặc xử lý sự cố hệ thống." // M&ANM
                )
        ));

        // Câu 9
        specializationQuestions.add(new Question(
                "Bạn muốn làm việc với dữ liệu ở mức độ nào?",
                Arrays.asList(
                        "A. Chỉ cần dữ liệu để hiển thị trên giao diện người dùng.", // CNPM
                        "B. Phân tích và quản lý dữ liệu lớn cho doanh nghiệp.", // HTTT
                        "C. Xử lý dữ liệu lớn để huấn luyện mô hình AI.", // AI
                        "D. Dữ liệu liên quan đến lưu lượng mạng hoặc bảo mật." // M&ANM
                )
        ));

        // Câu 10
        specializationQuestions.add(new Question(
                "Trong dài hạn, bạn muốn trở thành chuyên gia về lĩnh vực nào?",
                Arrays.asList(
                        "A. Phát triển và kiến trúc các giải pháp phần mềm phức tạp.", // CNPM
                        "B. Tư vấn và triển khai các hệ thống thông tin chiến lược cho doanh nghiệp.", // HTTT
                        "C. Nghiên cứu và ứng dụng trí tuệ nhân tạo vào các lĩnh vực khác nhau.", // AI
                        "D. Đảm bảo an toàn và phòng chống tấn công cho các hệ thống thông tin trọng yếu." // M&ANM
                )
        ));

        // Câu 11
        specializationQuestions.add(new Question(
                "Bạn có muốn làm việc với công nghệ mới nhất không?",
                Arrays.asList(
                        "A. Tôi muốn làm với công nghệ phát triển ứng dụng hiện đại (Flutter, React).", // CNPM
                        "B. Tôi thích công nghệ phân tích dữ liệu (Power BI, Tableau).", // HTTT
                        "C. Tôi muốn làm với công nghệ AI tiên tiến (TensorFlow, BERT).", // AI
                        "D. Tôi muốn làm với công nghệ đám mây (AWS, Azure)." // M&ANM
                )
        ));

        // Câu 12
        specializationQuestions.add(new Question(
                "Bạn muốn công việc tương lai có tính chất như thế nào?",
                Arrays.asList(
                        "A. Sáng tạo, liên tục phát triển sản phẩm mới.", // CNPM
                        "B. Ổn định, hỗ trợ doanh nghiệp tối ưu hóa quy trình.", // HTTT
                        "C. Nghiên cứu, tạo ra các giải pháp công nghệ đột phá.", // AI
                        "D. Kỹ thuật, đảm bảo hệ thống hoạt động ổn định." // M&ANM
                )
        ));
    }
}
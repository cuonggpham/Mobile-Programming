package com.example.emailapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var emailRecyclerView: RecyclerView
    private lateinit var composeFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailRecyclerView = findViewById(R.id.emailRecyclerView)
        composeFab = findViewById(R.id.composeFab)

        emailRecyclerView.layoutManager = LinearLayoutManager(this)
        emailRecyclerView.adapter = EmailAdapter(getSampleEmails())

        composeFab.setOnClickListener {
        }
    }

    private fun getSampleEmails(): List<Email> {
        return listOf(
            Email(
                senderName = "Phòng Đào Tạo",
                subject = "Thông báo đăng ký học phần học kỳ mới",
                preview = "Sinh viên vui lòng kiểm tra kế hoạch học tập và đăng ký môn đúng hạn...",
                time = "12:34 PM",
                avatarColor = Color.parseColor("#5E97F6"),
                isImportant = true
            ),
            Email(
                senderName = "Thầy Lê Bá Vui",
                subject = "Khảo sát góp ý để cải thiện chất lượng môn học",
                preview = "Các em vui lòng dành vài phút để hoàn thành khảo sát online...",
                time = "11:22 AM",
                avatarColor = Color.parseColor("#F4511E")
            ),
            Email(
                senderName = "CLB Công Nghệ",
                subject = "Workshop cuối tuần: Photoshop, SEO, Lập trình Web",
                preview = "Sự kiện miễn phí dành cho toàn bộ sinh viên, đăng ký ngay để giữ chỗ...",
                time = "11:04 AM",
                avatarColor = Color.parseColor("#9CCC65")
            ),
            Email(
                senderName = "Hỗ trợ Ký túc xá",
                subject = "Nhắc nhở: Gia hạn hợp đồng phòng ở",
                preview = "Vui lòng hoàn thành thủ tục gia hạn online trước thời hạn quy định...",
                time = "10:26 AM",
                avatarColor = Color.parseColor("#BDBDBD")
            ),
            Email(
                senderName = "Anh Mạnh – Ban Học Thuật",
                subject = "Tài liệu học tập mới đã được cập nhật",
                preview = "Các bạn có thể tải tài liệu mới nhất tại đường dẫn trong hệ thống LMS...",
                time = "9:45 AM",
                avatarColor = Color.parseColor("#9CCC65")
            ),
            Email(
                senderName = "Thư Viện Tạ Quang Bửu",
                subject = "Nhắc nhở trả sách",
                preview = "Vui lòng trả hoặc gia hạn sách mượn để tránh bị tính phí trễ...",
                time = "9:10 AM",
                avatarColor = Color.parseColor("#FFB74D")
            ),
            Email(
                senderName = "Trung Tâm Việc Làm",
                subject = "Cơ hội thực tập tháng này",
                preview = "Có nhiều vị trí thực tập trong lĩnh vực IT, marketing và thiết kế...",
                time = "8:32 AM",
                avatarColor = Color.parseColor("#4DD0E1")
            ),
            Email(
                senderName = "Hội Sinh Viên",
                subject = "Sự kiện âm nhạc cuối tuần",
                preview = "Mời bạn tham gia đêm nhạc với đồ ăn và hoạt động vui chơi...",
                time = "Hôm qua",
                avatarColor = Color.parseColor("#BA68C8")
            ),
            Email(
                senderName = "Trung Tâm Thể Thao",
                subject = "Lớp thể dục miễn phí cho sinh viên",
                preview = "Có các lớp yoga, zumba và tăng cơ. Đăng ký sớm để giữ chỗ...",
                time = "Hôm qua",
                avatarColor = Color.parseColor("#81C784")
            ),
            Email(
                senderName = "Hỗ Trợ Kỹ Thuật",
                subject = "Thông báo bảo trì hệ thống",
                preview = "Hệ thống trường sẽ bảo trì từ 1 giờ đến 3 giờ sáng...",
                time = "Thứ Hai",
                avatarColor = Color.parseColor("#90A4AE")
            )
        )
    }
}


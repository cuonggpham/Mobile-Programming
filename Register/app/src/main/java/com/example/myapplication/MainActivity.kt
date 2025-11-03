package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var etBirthday: EditText
    private lateinit var btnSelect: Button
    private lateinit var calendarView: CalendarView
    private lateinit var etAddress: EditText
    private lateinit var etEmail: EditText
    private lateinit var cbTerms: CheckBox
    private lateinit var btnRegister: Button

    private lateinit var calendarContainer: android.widget.FrameLayout
    private lateinit var scrollView: ScrollView

    private var isCalendarVisible = false
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        rgGender = findViewById(R.id.rgGender)
        etBirthday = findViewById(R.id.etBirthday)
        btnSelect = findViewById(R.id.btnSelect)
        calendarView = findViewById(R.id.calendarView)
        etAddress = findViewById(R.id.etAddress)
        etEmail = findViewById(R.id.etEmail)
        cbTerms = findViewById(R.id.cbTerms)
        btnRegister = findViewById(R.id.btnRegister)
        calendarContainer = findViewById(R.id.calendarContainer)
        scrollView = findViewById(R.id.scrollView)

        // Ẩn CalendarView ban đầu
        calendarContainer.visibility = View.GONE
        // Set ngày mặc định cho CalendarView
        calendarView.date = System.currentTimeMillis()

        // Ẩn/hiện CalendarView khi bấm Select
        btnSelect.setOnClickListener {
            toggleCalendarView()
        }

        // Xử lý khi chọn ngày trên CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val selectedDate = dateFormat.format(calendar.time)
            etBirthday.setText(selectedDate)
            // Ẩn CalendarView sau khi chọn ngày
            toggleCalendarView()
        }

        btnRegister.setOnClickListener {
            validateAndRegister()
        }
    }
    private fun toggleCalendarView() {
        isCalendarVisible = !isCalendarVisible
        if (isCalendarVisible) {
            calendarContainer.visibility = View.VISIBLE
            // Force invalidate để đảm bảo CalendarView được render
            calendarContainer.invalidate()
            calendarContainer.requestLayout()
            // Đợi layout render xong rồi mới scroll đến CalendarView
            scrollView.postDelayed({
                // Scroll để CalendarView hiển thị trong viewport
                val scrollY = scrollView.scrollY
                val calendarTop = calendarContainer.top
                val targetScroll = scrollY + calendarTop - scrollView.paddingTop
                scrollView.smoothScrollTo(0, targetScroll)
            }, 150)
        } else {
            calendarContainer.visibility = View.GONE
        }
    }

    private fun validateAndRegister() {
        var isValid = true

        etFirstName.setBackgroundColor(Color.parseColor("#E0E0E0"))
        etLastName.setBackgroundColor(Color.parseColor("#E0E0E0"))
        etBirthday.setBackgroundColor(Color.parseColor("#E0E0E0"))
        etAddress.setBackgroundColor(Color.parseColor("#E0E0E0"))
        etEmail.setBackgroundColor(Color.parseColor("#E0E0E0"))

        if (etFirstName.text.toString().trim().isEmpty()) {
            etFirstName.setBackgroundColor(Color.RED)
            isValid = false
        }

        if (etLastName.text.toString().trim().isEmpty()) {
            etLastName.setBackgroundColor(Color.RED)
            isValid = false
        }

        if (rgGender.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (etBirthday.text.toString().trim().isEmpty()) {
            etBirthday.setBackgroundColor(Color.RED)
            isValid = false
        }

        if (etAddress.text.toString().trim().isEmpty()) {
            etAddress.setBackgroundColor(Color.RED)
            isValid = false
        }

        if (etEmail.text.toString().trim().isEmpty()) {
            etEmail.setBackgroundColor(Color.RED)
            isValid = false
        }

        if (!cbTerms.isChecked) {
            Toast.makeText(this, "Please agree to Terms of Use", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (isValid) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
        }
    }
}
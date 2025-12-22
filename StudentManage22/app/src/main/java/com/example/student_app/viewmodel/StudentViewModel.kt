package com.example.student_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.student_app.Student

class StudentViewModel : ViewModel() {
    
    private val _students = MutableLiveData<MutableList<Student>>()
    val students: LiveData<MutableList<Student>> = _students
    
    // Dữ liệu tạm để binding với form thêm/sửa sinh viên
    var tempId = MutableLiveData("")
    var tempName = MutableLiveData("")
    var tempPhone = MutableLiveData("")
    var tempAddress = MutableLiveData("")
    
    // Vị trí sinh viên đang được chỉnh sửa
    var editingPosition = -1
    
    init {
        // Khởi tạo dữ liệu mẫu
        _students.value = mutableListOf(
            Student("Nguyễn Văn A", "20200001", "0901234567", "Hà Nội"),
            Student("Trần Thị B", "20200002", "0901234568", "Hải Phòng"),
            Student("Lê Văn C", "20200003", "0901234569", "Đà Nẵng")
        )
    }
    
    fun addStudent(student: Student) {
        val currentList = _students.value ?: mutableListOf()
        currentList.add(student)
        _students.value = currentList
    }
    
    fun updateStudent(position: Int, student: Student) {
        val currentList = _students.value ?: mutableListOf()
        if (position >= 0 && position < currentList.size) {
            currentList[position] = student
            _students.value = currentList
        }
    }
    
    fun deleteStudent(position: Int) {
        val currentList = _students.value ?: mutableListOf()
        if (position >= 0 && position < currentList.size) {
            currentList.removeAt(position)
            _students.value = currentList
        }
    }
    
    fun getStudent(position: Int): Student? {
        return _students.value?.getOrNull(position)
    }
    
    fun loadStudentForEditing(position: Int) {
        editingPosition = position
        val student = getStudent(position)
        if (student != null) {
            tempId.value = student.id
            tempName.value = student.name
            tempPhone.value = student.phone
            tempAddress.value = student.address
        }
    }
    
    fun clearTempData() {
        tempId.value = ""
        tempName.value = ""
        tempPhone.value = ""
        tempAddress.value = ""
        editingPosition = -1
    }
    
    fun createStudentFromTemp(): Student {
        return Student(
            name = tempName.value ?: "",
            id = tempId.value ?: "",
            phone = tempPhone.value ?: "",
            address = tempAddress.value ?: ""
        )
    }
    
    fun isValidInput(): Boolean {
        return !tempId.value.isNullOrBlank() && !tempName.value.isNullOrBlank()
    }
}

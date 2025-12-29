package com.example.student_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.student_app.Student
import com.example.student_app.data.StudentDatabase
import com.example.student_app.data.StudentRepository
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: StudentRepository
    val students: LiveData<List<Student>>

    var tempId = MutableLiveData("")
    var tempName = MutableLiveData("")
    var tempPhone = MutableLiveData("")
    var tempAddress = MutableLiveData("")

    var editingStudentId: String? = null
    
    init {
        val studentDao = StudentDatabase.getDatabase(application).studentDao()
        repository = StudentRepository(studentDao)
        students = repository.allStudents.asLiveData()
    }
    
    fun addStudent(student: Student) {
        viewModelScope.launch {
            repository.insert(student)
        }
    }
    
    fun updateStudent(student: Student) {
        viewModelScope.launch {
            repository.update(student)
        }
    }
    
    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.delete(student)
        }
    }
    
    fun deleteStudentById(id: String) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
    
    fun loadStudentForEditing(student: Student) {
        editingStudentId = student.id
        tempId.value = student.id
        tempName.value = student.name
        tempPhone.value = student.phone
        tempAddress.value = student.address
    }
    
    fun clearTempData() {
        tempId.value = ""
        tempName.value = ""
        tempPhone.value = ""
        tempAddress.value = ""
        editingStudentId = null
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

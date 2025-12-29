package com.example.student_app.data

import com.example.student_app.Student
import kotlinx.coroutines.flow.Flow

class StudentRepository(private val studentDao: StudentDao) {
    
    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()
    
    suspend fun insert(student: Student) {
        studentDao.insertStudent(student)
    }
    
    suspend fun update(student: Student) {
        studentDao.updateStudent(student)
    }
    
    suspend fun delete(student: Student) {
        studentDao.deleteStudent(student)
    }
    
    suspend fun deleteById(id: String) {
        studentDao.deleteStudentById(id)
    }
    
    suspend fun getById(id: String): Student? {
        return studentDao.getStudentById(id)
    }
}

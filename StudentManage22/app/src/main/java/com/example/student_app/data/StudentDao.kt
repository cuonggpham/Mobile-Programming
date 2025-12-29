package com.example.student_app.data

import androidx.room.*
import com.example.student_app.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>
    
    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: String): Student?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)
    
    @Update
    suspend fun updateStudent(student: Student)
    
    @Delete
    suspend fun deleteStudent(student: Student)
    
    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: String)
}

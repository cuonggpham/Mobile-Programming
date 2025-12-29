package com.example.student_app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.student_app.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class StudentDatabase : RoomDatabase() {
    
    abstract fun studentDao(): StudentDao
    
    companion object {
        @Volatile
        private var INSTANCE: StudentDatabase? = null
        
        fun getDatabase(context: Context): StudentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentDatabase::class.java,
                    "student_database"
                )
                .addCallback(StudentDatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        private class StudentDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.studentDao())
                    }
                }
            }
            
            suspend fun populateDatabase(studentDao: StudentDao) {
                // Thêm dữ liệu mẫu
                studentDao.insertStudent(Student("Nguyễn Văn A", "20220001", "0901234567", "Hà Nội"))
                studentDao.insertStudent(Student("Trần Thị B", "20220002", "0901234568", "Hải Phòng"))
                studentDao.insertStudent(Student("Lê Văn C", "20220003", "0901234569", "Đà Nẵng"))
            }
        }
    }
}

package com.example.filemanagement

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.filemanagement.databinding.ActivityTextViewerBinding
import java.io.File

class TextViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTextViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val filePath = intent.getStringExtra("FILE_PATH")
        if (filePath != null) {
            loadTextFile(filePath)
        } else {
            Toast.makeText(this, "Không thể mở file", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.fabEdit.setOnClickListener {
            Toast.makeText(this, "Chức năng chỉnh sửa đang phát triển", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadTextFile(filePath: String) {
        try {
            binding.loadingProgress.visibility = View.VISIBLE
            val file = File(filePath)

            binding.fileNameTextViewer.text = file.name
            binding.fileSizeTextViewer.text = formatFileSize(file.length())

            val content = file.readText()
            binding.textContent.text = content

            binding.loadingProgress.visibility = View.GONE
        } catch (e: Exception) {
            binding.loadingProgress.visibility = View.GONE
            Toast.makeText(this, "Lỗi khi đọc file: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        var fileSize = size.toDouble()
        var unitIndex = 0

        while (fileSize >= 1024 && unitIndex < units.size - 1) {
            fileSize /= 1024
            unitIndex++
        }

        return String.format("%.2f %s", fileSize, units[unitIndex])
    }
}

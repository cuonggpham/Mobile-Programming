package com.example.filemanagement

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.filemanagement.databinding.ActivityImageViewerBinding
import java.io.File

class ImageViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val filePath = intent.getStringExtra("FILE_PATH")
        if (filePath != null) {
            loadImage(filePath)
        } else {
            Toast.makeText(this, "Không thể mở ảnh", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.fabShare.setOnClickListener {
            Toast.makeText(this, "Chức năng chia sẻ đang phát triển", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImage(filePath: String) {
        try {
            binding.imageLoadingProgress.visibility = View.VISIBLE
            val file = File(filePath)

            binding.imageFileName.text = file.name

            // Load image
            val bitmap = BitmapFactory.decodeFile(filePath)
            if (bitmap != null) {
                binding.imageView.setImageBitmap(bitmap)
                val info = "${bitmap.width}x${bitmap.height} • ${formatFileSize(file.length())}"
                binding.imageInfo.text = info

                binding.imageLoadingProgress.visibility = View.GONE
                binding.errorMessage.visibility = View.GONE
            } else {
                throw Exception("Không thể decode ảnh")
            }
        } catch (e: Exception) {
            binding.imageLoadingProgress.visibility = View.GONE
            binding.errorMessage.visibility = View.VISIBLE
            binding.errorMessage.text = "Lỗi khi tải ảnh: ${e.message}"
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

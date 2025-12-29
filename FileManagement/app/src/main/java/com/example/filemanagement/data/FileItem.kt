package com.example.filemanagement.data

import java.io.File

/**
 * Data class đại diện cho file hoặc folder
 */
data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long
) {
    companion object {
        fun fromFile(file: File): FileItem {
            return FileItem(
                name = file.name,
                path = file.absolutePath,
                isDirectory = file.isDirectory,
                size = if (file.isDirectory) 0L else file.length(),
                lastModified = file.lastModified()
            )
        }
    }
    
    /**
     * Kiểm tra xem file có phải là file văn bản không
     */
    fun isTextFile(): Boolean {
        return !isDirectory && name.lowercase().endsWith(".txt")
    }
    
    /**
     * Kiểm tra xem file có phải là file ảnh không
     */
    fun isImageFile(): Boolean {
        if (isDirectory) return false
        val lowerName = name.lowercase()
        return lowerName.endsWith(".jpg") || 
               lowerName.endsWith(".jpeg") || 
               lowerName.endsWith(".png") || 
               lowerName.endsWith(".bmp")
    }
    
    /**
     * Lấy extension của file
     */
    fun getExtension(): String {
        val dotIndex = name.lastIndexOf('.')
        return if (dotIndex > 0) name.substring(dotIndex + 1).lowercase() else ""
    }
}

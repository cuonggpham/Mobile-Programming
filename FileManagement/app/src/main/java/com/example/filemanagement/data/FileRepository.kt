package com.example.filemanagement.data

import android.os.Environment
import java.io.File
import java.io.IOException

/**
 * Repository xử lý các thao tác với file system
 */
class FileRepository {
    
    /**
     * Lấy đường dẫn root của bộ nhớ ngoài
     */
    fun getExternalStorageRoot(): String {
        return Environment.getExternalStorageDirectory().absolutePath
    }
    
    /**
     * Kiểm tra xem external storage có thể đọc được không
     */
    fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        return state == Environment.MEDIA_MOUNTED || state == Environment.MEDIA_MOUNTED_READ_ONLY
    }
    
    /**
     * Kiểm tra xem external storage có thể ghi được không
     */
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
    
    /**
     * Liệt kê files/folders trong thư mục
     */
    fun listFiles(path: String): Result<List<FileItem>> {
        return try {
            val directory = File(path)
            if (!directory.exists()) {
                Result.failure(Exception("Thư mục không tồn tại"))
            } else if (!directory.isDirectory) {
                Result.failure(Exception("Đường dẫn không phải là thư mục"))
            } else if (!directory.canRead()) {
                Result.failure(Exception("Không có quyền đọc thư mục"))
            } else {
                val files = directory.listFiles()
                    ?.map { FileItem.fromFile(it) }
                    ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
                    ?: emptyList()
                Result.success(files)
            }
        } catch (e: SecurityException) {
            Result.failure(Exception("Không có quyền truy cập: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Đổi tên file/folder
     */
    fun renameFile(oldPath: String, newName: String): Result<String> {
        return try {
            val file = File(oldPath)
            if (!file.exists()) {
                Result.failure(Exception("File không tồn tại"))
            } else {
                val parentDir = file.parentFile
                val newFile = File(parentDir, newName)
                if (newFile.exists()) {
                    Result.failure(Exception("Đã tồn tại file/folder với tên này"))
                } else if (file.renameTo(newFile)) {
                    Result.success(newFile.absolutePath)
                } else {
                    Result.failure(Exception("Không thể đổi tên"))
                }
            }
        } catch (e: SecurityException) {
            Result.failure(Exception("Không có quyền: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Xóa file/folder
     */
    fun deleteFile(path: String): Result<Boolean> {
        return try {
            val file = File(path)
            if (!file.exists()) {
                Result.failure(Exception("File không tồn tại"))
            } else {
                val deleted = if (file.isDirectory) {
                    deleteRecursively(file)
                } else {
                    file.delete()
                }
                if (deleted) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("Không thể xóa"))
                }
            }
        } catch (e: SecurityException) {
            Result.failure(Exception("Không có quyền xóa: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Xóa folder và toàn bộ nội dung bên trong
     */
    private fun deleteRecursively(file: File): Boolean {
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                if (!deleteRecursively(child)) {
                    return false
                }
            }
        }
        return file.delete()
    }
    
    /**
     * Sao chép file đến thư mục khác
     */
    fun copyFile(sourcePath: String, destDirPath: String): Result<String> {
        return try {
            val sourceFile = File(sourcePath)
            if (!sourceFile.exists()) {
                Result.failure(Exception("File nguồn không tồn tại"))
            } else if (sourceFile.isDirectory) {
                Result.failure(Exception("Không hỗ trợ sao chép thư mục"))
            } else {
                val destDir = File(destDirPath)
                if (!destDir.exists() || !destDir.isDirectory) {
                    Result.failure(Exception("Thư mục đích không hợp lệ"))
                } else {
                    var destFile = File(destDir, sourceFile.name)
                    // Nếu file đích đã tồn tại, thêm số vào tên
                    var counter = 1
                    while (destFile.exists()) {
                        val nameWithoutExt = sourceFile.nameWithoutExtension
                        val ext = sourceFile.extension
                        val newName = if (ext.isNotEmpty()) {
                            "${nameWithoutExt}_$counter.$ext"
                        } else {
                            "${sourceFile.name}_$counter"
                        }
                        destFile = File(destDir, newName)
                        counter++
                    }
                    sourceFile.copyTo(destFile)
                    Result.success(destFile.absolutePath)
                }
            }
        } catch (e: IOException) {
            Result.failure(Exception("Lỗi khi sao chép: ${e.message}"))
        } catch (e: SecurityException) {
            Result.failure(Exception("Không có quyền: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Tạo thư mục mới
     */
    fun createFolder(parentPath: String, folderName: String): Result<String> {
        return try {
            val parentDir = File(parentPath)
            if (!parentDir.exists() || !parentDir.isDirectory) {
                Result.failure(Exception("Thư mục cha không hợp lệ"))
            } else {
                val newFolder = File(parentDir, folderName)
                if (newFolder.exists()) {
                    Result.failure(Exception("Thư mục đã tồn tại"))
                } else if (newFolder.mkdir()) {
                    Result.success(newFolder.absolutePath)
                } else {
                    Result.failure(Exception("Không thể tạo thư mục"))
                }
            }
        } catch (e: SecurityException) {
            Result.failure(Exception("Không có quyền tạo thư mục: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Tạo file văn bản mới
     */
    fun createTextFile(parentPath: String, fileName: String, content: String = ""): Result<String> {
        return try {
            val parentDir = File(parentPath)
            if (!parentDir.exists() || !parentDir.isDirectory) {
                Result.failure(Exception("Thư mục không hợp lệ"))
            } else {
                val actualFileName = if (fileName.lowercase().endsWith(".txt")) fileName else "$fileName.txt"
                val newFile = File(parentDir, actualFileName)
                if (newFile.exists()) {
                    Result.failure(Exception("File đã tồn tại"))
                } else {
                    newFile.writeText(content)
                    Result.success(newFile.absolutePath)
                }
            }
        } catch (e: IOException) {
            Result.failure(Exception("Lỗi khi tạo file: ${e.message}"))
        } catch (e: SecurityException) {
            Result.failure(Exception("Không có quyền tạo file: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Đọc nội dung file văn bản
     */
    fun readTextFile(path: String): Result<String> {
        return try {
            val file = File(path)
            if (!file.exists()) {
                Result.failure(Exception("File không tồn tại"))
            } else if (file.isDirectory) {
                Result.failure(Exception("Đây là thư mục, không phải file"))
            } else if (!file.canRead()) {
                Result.failure(Exception("Không có quyền đọc file"))
            } else {
                Result.success(file.readText())
            }
        } catch (e: IOException) {
            Result.failure(Exception("Lỗi khi đọc file: ${e.message}"))
        } catch (e: SecurityException) {
            Result.failure(Exception("Không có quyền đọc: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Lấy danh sách các thư mục con (dùng cho dialog chọn thư mục đích khi copy)
     */
    fun getSubFolders(path: String): Result<List<FileItem>> {
        return try {
            val directory = File(path)
            if (!directory.exists() || !directory.isDirectory) {
                Result.failure(Exception("Thư mục không hợp lệ"))
            } else {
                val folders = directory.listFiles()
                    ?.filter { it.isDirectory }
                    ?.map { FileItem.fromFile(it) }
                    ?.sortedBy { it.name.lowercase() }
                    ?: emptyList()
                Result.success(folders)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

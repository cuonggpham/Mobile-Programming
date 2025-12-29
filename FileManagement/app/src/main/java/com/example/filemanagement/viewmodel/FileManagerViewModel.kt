package com.example.filemanagement.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanagement.data.FileItem
import com.example.filemanagement.data.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** UI State cho FileListScreen */
data class FileManagerUiState(
        val currentPath: String = Environment.getExternalStorageDirectory().absolutePath,
        val files: List<FileItem> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val textFileContent: String? = null,
        val viewingFilePath: String? = null
)

/** ViewModel quản lý state cho ứng dụng quản lý file */
class FileManagerViewModel : ViewModel() {

    private val repository = FileRepository()

    private val _uiState = MutableStateFlow(FileManagerUiState())
    val uiState: StateFlow<FileManagerUiState> = _uiState.asStateFlow()

    private val rootPath: String = repository.getExternalStorageRoot()

    init {
        loadFiles(rootPath)
    }

    /** Load danh sách files trong thư mục */
    fun loadFiles(path: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = withContext(Dispatchers.IO) { repository.listFiles(path) }

            result.fold(
                    onSuccess = { files ->
                        _uiState.value =
                                _uiState.value.copy(
                                        currentPath = path,
                                        files = files,
                                        isLoading = false
                                )
                    },
                    onFailure = { error ->
                        _uiState.value =
                                _uiState.value.copy(
                                        isLoading = false,
                                        errorMessage = error.message ?: "Lỗi không xác định"
                                )
                    }
            )
        }
    }

    /** Chuyển đến thư mục được chọn */
    fun navigateTo(path: String) {
        loadFiles(path)
    }

    /** Quay lại thư mục cha */
    fun navigateUp(): Boolean {
        val currentPath = _uiState.value.currentPath
        if (currentPath == rootPath) {
            return false // Đã ở thư mục gốc
        }

        val parentPath = java.io.File(currentPath).parent
        if (parentPath != null && parentPath.startsWith(rootPath)) {
            loadFiles(parentPath)
            return true
        }
        return false
    }

    /** Kiểm tra xem có đang ở thư mục gốc không */
    fun isAtRoot(): Boolean {
        return _uiState.value.currentPath == rootPath
    }

    /** Đổi tên file/folder */
    fun renameFile(item: FileItem, newName: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { repository.renameFile(item.path, newName) }

            result.fold(
                    onSuccess = { loadFiles(_uiState.value.currentPath) },
                    onFailure = { error ->
                        _uiState.value =
                                _uiState.value.copy(errorMessage = "Lỗi đổi tên: ${error.message}")
                    }
            )
        }
    }

    /** Xóa file/folder */
    fun deleteFile(item: FileItem) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { repository.deleteFile(item.path) }

            result.fold(
                    onSuccess = { loadFiles(_uiState.value.currentPath) },
                    onFailure = { error ->
                        _uiState.value =
                                _uiState.value.copy(errorMessage = "Lỗi xóa file: ${error.message}")
                    }
            )
        }
    }

    /** Tạo thư mục mới */
    fun createFolder(name: String) {
        viewModelScope.launch {
            val result =
                    withContext(Dispatchers.IO) {
                        repository.createFolder(_uiState.value.currentPath, name)
                    }

            result.fold(
                    onSuccess = { loadFiles(_uiState.value.currentPath) },
                    onFailure = { error ->
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage = "Lỗi tạo folder: ${error.message}"
                                )
                    }
            )
        }
    }

    /** Tìm kiếm files */
    fun searchFiles(query: String) {
        if (query.isEmpty()) {
            loadFiles(_uiState.value.currentPath)
            return
        }

        viewModelScope.launch {
            val currentFiles = _uiState.value.files
            val filteredFiles =
                    withContext(Dispatchers.IO) {
                        currentFiles.filter { it.name.contains(query, ignoreCase = true) }
                    }

            _uiState.value = _uiState.value.copy(files = filteredFiles)
        }
    }

    /** Đổi tên file/folder (old method for compatibility) */
    fun renameItem(item: FileItem, newName: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { repository.renameFile(item.path, newName) }

            result.fold(
                    onSuccess = {
                        loadFiles(_uiState.value.currentPath)
                        onResult(true, null)
                    },
                    onFailure = { error -> onResult(false, error.message) }
            )
        }
    }

    /** Xóa file/folder */
    fun deleteItem(item: FileItem, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { repository.deleteFile(item.path) }

            result.fold(
                    onSuccess = {
                        loadFiles(_uiState.value.currentPath)
                        onResult(true, null)
                    },
                    onFailure = { error -> onResult(false, error.message) }
            )
        }
    }

    /** Sao chép file đến thư mục khác */
    fun copyFile(item: FileItem, destPath: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { repository.copyFile(item.path, destPath) }

            result.fold(
                    onSuccess = {
                        loadFiles(_uiState.value.currentPath)
                        onResult(true, null)
                    },
                    onFailure = { error -> onResult(false, error.message) }
            )
        }
    }

    /** Tạo thư mục mới */
    fun createFolder(name: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result =
                    withContext(Dispatchers.IO) {
                        repository.createFolder(_uiState.value.currentPath, name)
                    }

            result.fold(
                    onSuccess = {
                        loadFiles(_uiState.value.currentPath)
                        onResult(true, null)
                    },
                    onFailure = { error -> onResult(false, error.message) }
            )
        }
    }

    /** Tạo file văn bản mới */
    fun createTextFile(name: String, content: String = "", onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result =
                    withContext(Dispatchers.IO) {
                        repository.createTextFile(_uiState.value.currentPath, name, content)
                    }

            result.fold(
                    onSuccess = {
                        loadFiles(_uiState.value.currentPath)
                        onResult(true, null)
                    },
                    onFailure = { error -> onResult(false, error.message) }
            )
        }
    }

    /** Đọc nội dung file văn bản */
    fun readTextFile(path: String, onResult: (Boolean, String?, String?) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { repository.readTextFile(path) }

            result.fold(
                    onSuccess = { content ->
                        _uiState.value =
                                _uiState.value.copy(
                                        textFileContent = content,
                                        viewingFilePath = path
                                )
                        onResult(true, content, null)
                    },
                    onFailure = { error -> onResult(false, null, error.message) }
            )
        }
    }

    /** Clear text file content state */
    fun clearTextFileContent() {
        _uiState.value = _uiState.value.copy(textFileContent = null, viewingFilePath = null)
    }

    /** Lấy danh sách folders để chọn khi copy */
    fun getSubFolders(path: String, onResult: (List<FileItem>) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { repository.getSubFolders(path) }

            result.fold(
                    onSuccess = { folders -> onResult(folders) },
                    onFailure = { onResult(emptyList()) }
            )
        }
    }

    /** Clear error message */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /** Lấy đường dẫn root */
    fun getRootPath(): String = rootPath
}

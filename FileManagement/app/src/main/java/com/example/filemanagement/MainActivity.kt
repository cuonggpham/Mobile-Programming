package com.example.filemanagement

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanagement.adapter.FileAdapter
import com.example.filemanagement.databinding.ActivityMainBinding
import com.example.filemanagement.databinding.DialogCreateFolderBinding
import com.example.filemanagement.databinding.DialogCreateFileBinding
import com.example.filemanagement.databinding.DialogFileOptionsBinding
import com.example.filemanagement.databinding.DialogRenameFileBinding
import com.example.filemanagement.databinding.DialogSelectFolderBinding
import com.example.filemanagement.data.FileItem
import com.example.filemanagement.viewmodel.FileManagerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: FileManagerViewModel by viewModels()
    private lateinit var fileAdapter: FileAdapter
    
    // Launcher cho yêu cầu quyền storage truyền thống
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            viewModel.loadFiles(viewModel.getRootPath())
        } else {
            Toast.makeText(
                this,
                "Cần cấp quyền truy cập bộ nhớ để sử dụng ứng dụng",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    // Launcher cho MANAGE_EXTERNAL_STORAGE (Android 11+)
    private val manageStorageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                viewModel.loadFiles(viewModel.getRootPath())
            } else {
                Toast.makeText(
                    this,
                    "Cần cấp quyền quản lý tất cả file để sử dụng ứng dụng",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupSearchBar()
        setupFab()
        setupObservers()
        
        // Kiểm tra và yêu cầu quyền
        checkAndRequestPermissions()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.homeButton.setOnClickListener {
            viewModel.loadFiles(viewModel.getRootPath())
        }
    }
    
    private fun setupRecyclerView() {
        fileAdapter = FileAdapter(
            onItemClick = { fileItem ->
                if (fileItem.isDirectory) {
                    viewModel.navigateTo(fileItem.path)
                } else {
                    openFile(fileItem)
                }
            },
            onItemLongClick = { fileItem ->
                showFileOptions(fileItem)
            }
        )
        
        binding.fileRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = fileAdapter
        }
    }
    
    private fun setupSearchBar() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchFiles(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun setupFab() {
        binding.fabMenu.setOnClickListener {
            showCreateOptionsMenu()
        }
    }
    
    private fun showCreateOptionsMenu() {
        val options = arrayOf("Tạo Folder", "Tạo File TXT")
        MaterialAlertDialogBuilder(this)
            .setTitle("Tạo mới")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showCreateFolderDialog()
                    1 -> showCreateFileDialog()
                }
            }
            .show()
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUI(state)
            }
        }
    }
    
    private fun updateUI(state: com.example.filemanagement.viewmodel.FileManagerUiState) {
        // Update current path
        binding.currentPathText.text = state.currentPath
        
        // Update file list
        fileAdapter.submitList(state.files)
        
        // Show/hide loading
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        
        // Show/hide empty state
        binding.emptyStateLayout.visibility = 
            if (state.files.isEmpty() && !state.isLoading) View.VISIBLE else View.GONE
        
        // Show error message
        state.errorMessage?.let {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openFile(fileItem: FileItem) {
        when {
            fileItem.isTextFile() -> {
                val intent = Intent(this, TextViewerActivity::class.java).apply {
                    putExtra("FILE_PATH", fileItem.path)
                }
                startActivity(intent)
            }
            fileItem.isImageFile() -> {
                val intent = Intent(this, ImageViewerActivity::class.java).apply {
                    putExtra("FILE_PATH", fileItem.path)
                }
                startActivity(intent)
            }
            else -> {
                Toast.makeText(this, "Không thể mở file này", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showFileOptions(fileItem: FileItem) {
        val dialogBinding = DialogFileOptionsBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()
        
        dialogBinding.fileNameDialog.text = fileItem.name
        dialogBinding.fileSizeDialog.text = formatFileSize(fileItem.size)
        
        dialogBinding.openOption.setOnClickListener {
            openFile(fileItem)
            dialog.dismiss()
        }
        
        dialogBinding.shareOption.setOnClickListener {
            shareFile(fileItem)
            dialog.dismiss()
        }
        
        dialogBinding.renameOption.setOnClickListener {
            showRenameDialog(fileItem)
            dialog.dismiss()
        }
        
        dialogBinding.copyOption.setOnClickListener {
            if (fileItem.isDirectory) {
                Toast.makeText(this, "Không thể sao chép thư mục", Toast.LENGTH_SHORT).show()
            } else {
                showSelectFolderDialog(fileItem)
            }
            dialog.dismiss()
        }
        
        dialogBinding.deleteOption.setOnClickListener {
            showDeleteConfirmation(fileItem)
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun showCreateFolderDialog() {
        val dialogBinding = DialogCreateFolderBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()
        
        dialogBinding.createButton.setOnClickListener {
            val folderName = dialogBinding.folderNameEditText.text.toString().trim()
            if (folderName.isNotEmpty()) {
                viewModel.createFolder(folderName) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Đã tạo folder thành công", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, error ?: "Lỗi tạo folder", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            } else {
                dialogBinding.folderNameLayout.error = "Vui lòng nhập tên folder"
            }
        }
        
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun showCreateFileDialog() {
        val dialogBinding = DialogCreateFileBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()
        
        dialogBinding.createButton.setOnClickListener {
            val fileName = dialogBinding.fileNameEditText.text.toString().trim()
            val fileContent = dialogBinding.fileContentEditText.text.toString()
            
            if (fileName.isNotEmpty()) {
                viewModel.createTextFile(fileName, fileContent) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Đã tạo file thành công", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, error ?: "Lỗi tạo file", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            } else {
                dialogBinding.fileNameLayout.error = "Vui lòng nhập tên file"
            }
        }
        
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun showRenameDialog(fileItem: FileItem) {
        val dialogBinding = DialogRenameFileBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()
        
        dialogBinding.currentFileName.text = fileItem.name
        dialogBinding.newNameEditText.setText(fileItem.name.substringBeforeLast("."))
        
        dialogBinding.renameConfirmButton.setOnClickListener {
            val newName = dialogBinding.newNameEditText.text.toString().trim()
            if (newName.isNotEmpty()) {
                val extension = fileItem.getExtension()
                val fullName = if (extension.isNotEmpty()) "$newName.$extension" else newName
                viewModel.renameFile(fileItem, fullName)
                dialog.dismiss()
            } else {
                dialogBinding.newNameLayout.error = "Vui lòng nhập tên mới"
            }
        }
        
        dialogBinding.renameCancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun showDeleteConfirmation(fileItem: FileItem) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa ${fileItem.name}?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.deleteFile(fileItem)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
    
    private fun shareFile(fileItem: FileItem) {
        // Implement share functionality
        Toast.makeText(this, "Chia sẻ ${fileItem.name}", Toast.LENGTH_SHORT).show()
    }
    
    private fun showSelectFolderDialog(fileItem: FileItem) {
        val dialogBinding = DialogSelectFolderBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()
        
        var currentPath = viewModel.getRootPath()
        val folderAdapter = FileAdapter(
            onItemClick = { folder ->
                // Điều hướng vào thư mục con
                currentPath = folder.path
                loadFoldersForDialog(dialogBinding, currentPath, fileItem)
            },
            onItemLongClick = { } // Không cần long click trong dialog chọn folder
        )
        
        dialogBinding.foldersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = folderAdapter
        }
        
        // Load folders ban đầu
        loadFoldersForDialog(dialogBinding, currentPath, fileItem)
        
        // Cập nhật path hiển thị
        dialogBinding.currentPathText.text = currentPath
        
        // Nút lên thư mục cha
        dialogBinding.upButton.setOnClickListener {
            val parentPath = java.io.File(currentPath).parent
            if (parentPath != null && parentPath != "/") {
                currentPath = parentPath
                loadFoldersForDialog(dialogBinding, currentPath, fileItem)
                dialogBinding.currentPathText.text = currentPath
            }
        }
        
        // Nút về home
        dialogBinding.homeButton.setOnClickListener {
            currentPath = viewModel.getRootPath()
            loadFoldersForDialog(dialogBinding, currentPath, fileItem)
            dialogBinding.currentPathText.text = currentPath
        }
        
        // Chọn thư mục hiện tại
        dialogBinding.selectCurrentButton.setOnClickListener {
            viewModel.copyFile(fileItem, currentPath) { success, error ->
                if (success) {
                    Toast.makeText(this, "Đã sao chép file thành công", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, error ?: "Lỗi sao chép file", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun loadFoldersForDialog(
        dialogBinding: DialogSelectFolderBinding,
        path: String,
        fileItem: FileItem
    ) {
        viewModel.getSubFolders(path) { folders ->
            // Lọc bỏ thư mục chứa file nguồn để tránh copy vào chính nó
            val filteredFolders = folders.filter { 
                !fileItem.path.startsWith(it.path)
            }
            (dialogBinding.foldersRecyclerView.adapter as? FileAdapter)?.submitList(filteredFolders)
            dialogBinding.currentPathText.text = path
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
    
    override fun onBackPressed() {
        if (!viewModel.navigateUp()) {
            super.onBackPressed()
        }
    }
    
    private fun checkAndRequestPermissions() {
        when {
            // Android 11+ (API 30+): Cần MANAGE_EXTERNAL_STORAGE
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                if (!Environment.isExternalStorageManager()) {
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                            data = Uri.parse("package:$packageName")
                        }
                        manageStorageLauncher.launch(intent)
                    } catch (e: Exception) {
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        manageStorageLauncher.launch(intent)
                    }
                }
            }
            // Android 6-10: Cần READ_EXTERNAL_STORAGE
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                
                val allGranted = permissions.all {
                    checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
                }
                
                if (!allGranted) {
                    requestPermissionLauncher.launch(permissions)
                }
            }
            // Android 5 và thấp hơn: Quyền được cấp khi cài đặt
            else -> {
                // Không cần xử lý gì
            }
        }
    }
}
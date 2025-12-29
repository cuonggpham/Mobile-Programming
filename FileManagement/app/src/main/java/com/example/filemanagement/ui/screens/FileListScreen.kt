package com.example.filemanagement.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.filemanagement.data.FileItem
import com.example.filemanagement.ui.components.*
import com.example.filemanagement.viewmodel.FileManagerViewModel

/**
 * Màn hình chính hiển thị danh sách file/folder
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileListScreen(
    viewModel: FileManagerViewModel,
    onViewTextFile: (String, String) -> Unit,
    onViewImageFile: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // States cho context menu và dialogs
    var selectedItem by remember { mutableStateOf<FileItem?>(null) }
    var showContextMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCopyDialog by remember { mutableStateOf(false) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showCreateTextFileDialog by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    
    // Tính toán đường dẫn hiển thị (tương đối với root)
    val displayPath = remember(uiState.currentPath) {
        uiState.currentPath.removePrefix(viewModel.getRootPath()).ifEmpty { "/" }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = "Quản lý File",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = displayPath,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    if (!viewModel.isAtRoot()) {
                        IconButton(onClick = { viewModel.navigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại"
                            )
                        }
                    }
                },
                actions = {
                    // Options menu button
                    IconButton(onClick = { showOptionsMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    
                    // Options dropdown menu
                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = { showOptionsMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tạo thư mục mới") },
                            onClick = {
                                showOptionsMenu = false
                                showCreateFolderDialog = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.CreateNewFolder, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Tạo file văn bản mới") },
                            onClick = {
                                showOptionsMenu = false
                                showCreateTextFileDialog = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.NoteAdd, contentDescription = null)
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Làm mới") },
                            onClick = {
                                showOptionsMenu = false
                                viewModel.loadFiles(uiState.currentPath)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadFiles(uiState.currentPath) }) {
                            Text("Thử lại")
                        }
                    }
                }
                uiState.files.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Thư mục trống",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = uiState.files,
                            key = { it.path }
                        ) { item ->
                            Box {
                                FileListItem(
                                    item = item,
                                    onClick = {
                                        when {
                                            item.isDirectory -> viewModel.navigateTo(item.path)
                                            item.isTextFile() -> {
                                                viewModel.readTextFile(item.path) { success, content, error ->
                                                    if (success && content != null) {
                                                        onViewTextFile(item.path, content)
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            error ?: "Không thể đọc file",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }
                                            item.isImageFile() -> onViewImageFile(item.path)
                                            else -> {
                                                Toast.makeText(
                                                    context,
                                                    "Không hỗ trợ xem loại file này",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    onLongClick = {
                                        selectedItem = item
                                        showContextMenu = true
                                    }
                                )
                                
                                // Context menu cho item được chọn
                                if (showContextMenu && selectedItem == item) {
                                    FileContextMenu(
                                        item = item,
                                        expanded = true,
                                        onDismiss = { 
                                            showContextMenu = false
                                            selectedItem = null
                                        },
                                        onRename = { showRenameDialog = true },
                                        onDelete = { showDeleteDialog = true },
                                        onCopy = if (!item.isDirectory) {
                                            { showCopyDialog = true }
                                        } else null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Dialogs
    
    // Rename dialog
    if (showRenameDialog && selectedItem != null) {
        RenameDialog(
            item = selectedItem!!,
            onDismiss = {
                showRenameDialog = false
                selectedItem = null
            },
            onConfirm = { newName ->
                viewModel.renameItem(selectedItem!!, newName) { success, error ->
                    if (success) {
                        Toast.makeText(context, "Đổi tên thành công", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, error ?: "Lỗi đổi tên", Toast.LENGTH_SHORT).show()
                    }
                }
                showRenameDialog = false
                selectedItem = null
            }
        )
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog && selectedItem != null) {
        DeleteConfirmDialog(
            item = selectedItem!!,
            onDismiss = {
                showDeleteDialog = false
                selectedItem = null
            },
            onConfirm = {
                viewModel.deleteItem(selectedItem!!) { success, error ->
                    if (success) {
                        Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, error ?: "Lỗi xóa", Toast.LENGTH_SHORT).show()
                    }
                }
                showDeleteDialog = false
                selectedItem = null
            }
        )
    }
    
    // Copy file dialog
    if (showCopyDialog && selectedItem != null) {
        CopyFileDialog(
            item = selectedItem!!,
            currentPath = uiState.currentPath,
            rootPath = viewModel.getRootPath(),
            onGetSubFolders = { path, callback ->
                viewModel.getSubFolders(path, callback)
            },
            onDismiss = {
                showCopyDialog = false
                selectedItem = null
            },
            onConfirm = { destPath ->
                viewModel.copyFile(selectedItem!!, destPath) { success, error ->
                    if (success) {
                        Toast.makeText(context, "Sao chép thành công", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, error ?: "Lỗi sao chép", Toast.LENGTH_SHORT).show()
                    }
                }
                showCopyDialog = false
                selectedItem = null
            }
        )
    }
    
    // Create folder dialog
    if (showCreateFolderDialog) {
        CreateFolderDialog(
            onDismiss = { showCreateFolderDialog = false },
            onConfirm = { folderName ->
                viewModel.createFolder(folderName) { success, error ->
                    if (success) {
                        Toast.makeText(context, "Tạo thư mục thành công", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, error ?: "Lỗi tạo thư mục", Toast.LENGTH_SHORT).show()
                    }
                }
                showCreateFolderDialog = false
            }
        )
    }
    
    // Create text file dialog
    if (showCreateTextFileDialog) {
        CreateTextFileDialog(
            onDismiss = { showCreateTextFileDialog = false },
            onConfirm = { fileName, content ->
                viewModel.createTextFile(fileName, content) { success, error ->
                    if (success) {
                        Toast.makeText(context, "Tạo file thành công", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, error ?: "Lỗi tạo file", Toast.LENGTH_SHORT).show()
                    }
                }
                showCreateTextFileDialog = false
            }
        )
    }
}

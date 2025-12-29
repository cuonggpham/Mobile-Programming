package com.example.filemanagement.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.filemanagement.data.FileItem

/**
 * Dialog đổi tên file/folder
 */
@Composable
fun RenameDialog(
    item: FileItem,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf(item.name) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đổi tên") },
        text = {
            Column {
                Text("Nhập tên mới cho ${if (item.isDirectory) "thư mục" else "file"}:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(newName) },
                enabled = newName.isNotBlank() && newName != item.name
            ) {
                Text("Đổi tên")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

/**
 * Dialog xác nhận xóa
 */
@Composable
fun DeleteConfirmDialog(
    item: FileItem,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xác nhận xóa") },
        text = {
            Text(
                if (item.isDirectory) {
                    "Bạn có chắc chắn muốn xóa thư mục \"${item.name}\" và toàn bộ nội dung bên trong?"
                } else {
                    "Bạn có chắc chắn muốn xóa file \"${item.name}\"?"
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Xóa")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

/**
 * Dialog chọn thư mục đích để copy
 */
@Composable
fun CopyFileDialog(
    item: FileItem,
    currentPath: String,
    rootPath: String,
    onGetSubFolders: (String, (List<FileItem>) -> Unit) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedPath by remember { mutableStateOf(currentPath) }
    var folders by remember { mutableStateOf<List<FileItem>>(emptyList()) }
    
    // Load folders khi path thay đổi
    LaunchedEffect(selectedPath) {
        onGetSubFolders(selectedPath) { folderList ->
            folders = folderList
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sao chép đến") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                // Hiển thị đường dẫn hiện tại
                Text(
                    text = "Đường dẫn: ${selectedPath.removePrefix(rootPath).ifEmpty { "/" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Nút quay lại
                if (selectedPath != rootPath) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val parent = java.io.File(selectedPath).parent
                                if (parent != null && parent.startsWith(rootPath)) {
                                    selectedPath = parent
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Quay lại", color = MaterialTheme.colorScheme.primary)
                    }
                }
                
                HorizontalDivider()
                
                // Danh sách folders
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(folders) { folder ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPath = folder.path }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Folder,
                                contentDescription = "Folder",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(folder.name)
                        }
                    }
                    
                    if (folders.isEmpty()) {
                        item {
                            Text(
                                text = "Không có thư mục con",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedPath) },
                enabled = selectedPath != currentPath
            ) {
                Text("Sao chép vào đây")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

/**
 * Dialog tạo thư mục mới
 */
@Composable
fun CreateFolderDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var folderName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo thư mục mới") },
        icon = { Icon(Icons.Default.CreateNewFolder, contentDescription = null) },
        text = {
            Column {
                Text("Nhập tên thư mục:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = folderName,
                    onValueChange = { folderName = it },
                    singleLine = true,
                    placeholder = { Text("Tên thư mục") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(folderName) },
                enabled = folderName.isNotBlank()
            ) {
                Text("Tạo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

/**
 * Dialog tạo file văn bản mới
 */
@Composable
fun CreateTextFileDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo file văn bản mới") },
        text = {
            Column {
                Text("Tên file:")
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    singleLine = true,
                    placeholder = { Text("example.txt") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("Nội dung (tùy chọn):")
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("Nhập nội dung...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(fileName, content) },
                enabled = fileName.isNotBlank()
            ) {
                Text("Tạo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

/**
 * Dialog hiển thị thông báo lỗi
 */
@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lỗi") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

/**
 * Dialog hiển thị thông báo thành công
 */
@Composable
fun SuccessSnackbar(
    message: String,
    onDismiss: () -> Unit
) {
    Snackbar(
        action = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    ) {
        Text(message)
    }
}

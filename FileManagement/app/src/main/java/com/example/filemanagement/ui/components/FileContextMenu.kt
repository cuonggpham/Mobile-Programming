package com.example.filemanagement.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.filemanagement.data.FileItem

/**
 * Context menu cho file hoặc folder
 */
@Composable
fun FileContextMenu(
    item: FileItem,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onCopy: (() -> Unit)? = null // Chỉ có với file, không có với folder
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = DpOffset(16.dp, 0.dp)
    ) {
        // Đổi tên
        DropdownMenuItem(
            text = { Text("Đổi tên") },
            onClick = {
                onDismiss()
                onRename()
            },
            leadingIcon = {
                Icon(Icons.Default.Edit, contentDescription = "Đổi tên")
            }
        )
        
        // Xóa
        DropdownMenuItem(
            text = { Text("Xóa") },
            onClick = {
                onDismiss()
                onDelete()
            },
            leadingIcon = {
                Icon(Icons.Default.Delete, contentDescription = "Xóa")
            }
        )
        
        // Sao chép (chỉ với file)
        if (!item.isDirectory && onCopy != null) {
            DropdownMenuItem(
                text = { Text("Sao chép đến...") },
                onClick = {
                    onDismiss()
                    onCopy()
                },
                leadingIcon = {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Sao chép")
                }
            )
        }
    }
}

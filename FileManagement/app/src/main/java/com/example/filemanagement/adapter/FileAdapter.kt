package com.example.filemanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanagement.R
import com.example.filemanagement.data.FileItem
import com.example.filemanagement.databinding.ItemFileBinding
import java.text.SimpleDateFormat
import java.util.*

class FileAdapter(
        private val onItemClick: (FileItem) -> Unit,
        private val onItemLongClick: (FileItem) -> Unit
) : ListAdapter<FileItem, FileAdapter.FileViewHolder>(FileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding, onItemClick, onItemLongClick)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FileViewHolder(
            private val binding: ItemFileBinding,
            private val onItemClick: (FileItem) -> Unit,
            private val onItemLongClick: (FileItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(fileItem: FileItem) {
            binding.fileNameText.text = fileItem.name

            // Set icon based on file type
            val iconRes =
                    when {
                        fileItem.isDirectory -> R.drawable.ic_folder
                        fileItem.isImageFile() -> R.drawable.ic_image
                        fileItem.isTextFile() -> R.drawable.ic_text
                        else -> R.drawable.ic_file
                    }
            binding.fileIcon.setImageResource(iconRes)

            // Set file info
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val dateStr = dateFormat.format(Date(fileItem.lastModified))

            val info =
                    if (fileItem.isDirectory) {
                        "Folder • $dateStr"
                    } else {
                        "${formatFileSize(fileItem.size)} • $dateStr"
                    }
            binding.fileInfoText.text = info

            // Click listeners
            binding.root.setOnClickListener { onItemClick(fileItem) }

            binding.root.setOnLongClickListener {
                onItemLongClick(fileItem)
                true
            }

            binding.moreButton.setOnClickListener { onItemLongClick(fileItem) }
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

            return String.format("%.1f %s", fileSize, units[unitIndex])
        }
    }

    private class FileDiffCallback : DiffUtil.ItemCallback<FileItem>() {
        override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem == newItem
        }
    }
}

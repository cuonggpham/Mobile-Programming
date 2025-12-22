package com.example.student_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.student_app.R
import com.example.student_app.Student

class StudentRecyclerAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : ListAdapter<Student, StudentRecyclerAdapter.StudentViewHolder>(StudentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_item, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = getItem(position)
        holder.bind(student)
        
        holder.itemView.setOnClickListener {
            onItemClick(holder.adapterPosition)
        }
        
        holder.btnDelete.setOnClickListener {
            onDelete(holder.adapterPosition)
        }
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvId: TextView = itemView.findViewById(R.id.tvId)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)

        fun bind(student: Student) {
            tvName.text = student.name
            tvId.text = student.id
        }
    }

    class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }
    }
}

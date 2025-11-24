package com.example.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(
        private val students: MutableList<StudentModel>,
        private val onItemClick: (StudentModel) -> Unit,
        private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvStudentId: TextView = itemView.findViewById(R.id.tvStudentId)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(students[position])
                }
            }

            btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(position)
                }
            }
        }

        fun bind(student: StudentModel) {
            tvStudentName.text = student.name
            tvStudentId.text = student.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount(): Int = students.size

    fun addStudent(student: StudentModel) {
        students.add(student)
        notifyItemInserted(students.size - 1)
    }

    fun updateStudent(position: Int, student: StudentModel) {
        if (position in students.indices) {
            students[position] = student
            notifyItemChanged(position)
        }
    }

    fun removeStudent(position: Int) {
        if (position in students.indices) {
            students.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getStudentPosition(student: StudentModel): Int {
        return students.indexOf(student)
    }
}

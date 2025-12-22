package com.example.student_app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.student_app.R
import com.example.student_app.adapter.StudentRecyclerAdapter
import com.example.student_app.databinding.FragmentStudentListBinding
import com.example.student_app.viewmodel.StudentViewModel

class StudentListFragment : Fragment() {

    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: StudentViewModel by activityViewModels()
    private lateinit var adapter: StudentRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeStudents()
        setupFab()
    }

    private fun setupRecyclerView() {
        adapter = StudentRecyclerAdapter(
            onItemClick = { position ->
                viewModel.loadStudentForEditing(position)
                val bundle = Bundle().apply {
                    putInt("studentPosition", position)
                }
                findNavController().navigate(R.id.action_list_to_update, bundle)
            },
            onDelete = { position ->
                viewModel.deleteStudent(position)
            }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun observeStudents() {
        viewModel.students.observe(viewLifecycleOwner) { students ->
            adapter.submitList(students.toList())
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            viewModel.clearTempData()
            findNavController().navigate(R.id.action_list_to_add)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

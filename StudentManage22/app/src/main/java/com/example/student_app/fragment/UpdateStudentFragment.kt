package com.example.student_app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.student_app.databinding.FragmentUpdateStudentBinding
import com.example.student_app.viewmodel.StudentViewModel

class UpdateStudentFragment : Fragment() {

    private var _binding: FragmentUpdateStudentBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: StudentViewModel by activityViewModels()
    private var studentPosition: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateStudentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Lấy vị trí sinh viên từ arguments
        studentPosition = arguments?.getInt("studentPosition", -1) ?: -1
        
        binding.btnUpdate.setOnClickListener {
            if (viewModel.isValidInput()) {
                val updatedStudent = viewModel.createStudentFromTemp()
                viewModel.updateStudent(viewModel.editingPosition, updatedStudent)
                viewModel.clearTempData()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

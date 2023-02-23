package com.alexc.todoapp.task.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alexc.todoapp.R
import com.alexc.todoapp.databinding.FragmentFormTaskBinding
import com.alexc.todoapp.task.helper.FirebaseHelper
import com.alexc.todoapp.task.model.Task

class FormTaskFragment : Fragment() {

    private val args: FormTaskFragmentArgs by navArgs()

    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: Task
    private var newTask: Boolean = true
    private var statusTask: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFormTaskBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        getArgs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private  fun initListeners(){
        binding.btnSave.setOnClickListener { validateData() }

        binding.radioGroup.setOnCheckedChangeListener{ _, id ->
            statusTask = when(id){
                R.id.rbTodo -> 0
                R.id.rbDoing -> 1
                else -> 2
            }

        }
    }

    private fun validateData(){
        val description = binding.txtDescription.text.toString().trim()

        if(description.isNotEmpty()){
            binding.progressBar.isVisible = true

            if(newTask) task = Task()
            task.description = description
            task.status = statusTask

            saveTask()
        }
        else{
            Toast.makeText(requireContext(), "Enter a description", Toast.LENGTH_LONG).show()
        }
    }

    private fun getArgs() {
        args.task.let {
            if (it != null) {
                task = it
                configTask()
            }
        }
    }

    private fun configTask(){
        newTask = false
        statusTask = task.status
        binding.txtToolbar.text = R.string.edit_task.toString()

        binding.txtDescription.setText(task.description)
        setStatus()
    }

    private fun setStatus(){
        binding.radioGroup.check(
            when(task.status){
                0-> {
                    R.id.rbTodo
                }
                2-> {
                    R.id.rbDoing
                }
                else -> {
                    R.id.rbDone
                }
            }
        )
    }

    private fun saveTask(){
        FirebaseHelper
            .getDataBase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    if(newTask){// add task
                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), "Task saved successfully", Toast.LENGTH_LONG).show()
                    }
                    else{ // Edit task
                        binding.progressBar.isVisible = false
                        Toast.makeText(requireContext(), "Task updated with satisfaction", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(requireContext(), "Failed to save task", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener{
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), "Failed to save task", Toast.LENGTH_LONG).show()
            }

    }

}
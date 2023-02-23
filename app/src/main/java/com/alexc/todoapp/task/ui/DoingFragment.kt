package com.alexc.todoapp.task.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexc.todoapp.R
import com.alexc.todoapp.databinding.FragmentDoingBinding
import com.alexc.todoapp.task.helper.FirebaseHelper
import com.alexc.todoapp.task.model.Task
import com.alexc.todoapp.task.ui.adapter.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DoingFragment : Fragment() {

    private var _binding: FragmentDoingBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter

    private val taskList = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClick()
        getTask()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initClick(){

    }

    private fun getTask(){
        FirebaseHelper
            .getDataBase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?:"")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        taskList.clear()
                        for (snap in snapshot.children) {
                            val task = snap.getValue(Task::class.java) as Task
                            if(task.status == 1) {
                                taskList.add(task)
                            }
                        }
                        taskList.reverse()
                        initAdapter()
                    }
                    else{
                        binding.txtLoadingTask.text = "No task found"
                    }
                    taskEmpty()
                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error List Task", Toast.LENGTH_LONG).show()

                }
            })
    }

    private fun initAdapter(){
        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTask.setHasFixedSize(true)
        taskAdapter = TaskAdapter(requireContext(), taskList){ task, select ->
            optionSelect(task,select)
        }
        binding.rvTask.adapter = taskAdapter
    }

    private fun optionSelect(task: Task, select :Int){
        when(select){
            TaskAdapter.SELECT_REMOVE -> {
                deleteTask(task)
            }
            TaskAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFormTaskFragment(task)
                findNavController().navigate(action)
            }
            TaskAdapter.SELECT_NEXT -> {
                task.status = 2
                updateTask(task)
            }
            TaskAdapter.SELECT_BACK -> {
                task.status = 0
                updateTask(task)
            }
        }
    }

    private fun deleteTask(task: Task){
        FirebaseHelper
            .getDataBase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?:"")
            .child(task.id)
            .removeValue()

        taskList.remove(task)
        taskAdapter.notifyDataSetChanged()
    }

    private fun updateTask(task: Task){
        FirebaseHelper
            .getDataBase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    Toast.makeText(requireContext(), "Task updated to 'DOING' with satisfaction", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(requireContext(), "Failed to save task", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener{
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), "Failed to save task", Toast.LENGTH_LONG).show()
            }
    }

    private fun taskEmpty(){
        binding.txtLoadingTask.text = if(taskList.isEmpty()){
            getText(R.string.no_tasks)
        }else{
            ""
        }
    }

}
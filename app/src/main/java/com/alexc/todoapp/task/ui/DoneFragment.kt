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
import com.alexc.todoapp.databinding.FragmentDoneBinding
import com.alexc.todoapp.task.helper.FirebaseHelper
import com.alexc.todoapp.task.model.Task
import com.alexc.todoapp.task.ui.adapter.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DoneFragment : Fragment() {

    private var _binding: FragmentDoneBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter

    private val taskList = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoneBinding.inflate(inflater,container,false)
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
                            if(task.status == 2) {
                                taskList.add(task)
                            }
                        }
                        binding.txtLoadingTask.text = ""
                        initAdapter()
                    }
                    else{
                        binding.txtLoadingTask.text = "No task found"
                    }
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
}
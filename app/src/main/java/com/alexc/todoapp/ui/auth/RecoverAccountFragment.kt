package com.alexc.todoapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.alexc.todoapp.R
import com.alexc.todoapp.databinding.FragmentRecoverAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverAccountFragment : Fragment() {

    private var _binding: FragmentRecoverAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecoverAccountBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        initClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initClick(){
        binding.btnSend.setOnClickListener { validateData() }
    }

    private fun validateData(){
        val email = binding.txtEmail.text.toString().trim()

        if(email.isNotEmpty()){
            recoverAccountUser(email)
        }
        else{
            Toast.makeText(requireContext(),"Enter Email", Toast.LENGTH_LONG).show()
        }
    }

    private fun recoverAccountUser(email:String){
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(),"A link was sent to your email",Toast.LENGTH_LONG).show()
                } else {
                    binding.progressBar.isVisible = false
                }
            }
    }
}
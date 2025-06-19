package com.example.mygaurdian.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mygaurdian.MainActivity
import com.example.mygaurdian.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSignUpBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val pass = binding.etPassword.text.toString()
            val confirm = binding.etConfirmPassword.text.toString()
            if (email.isEmpty() || phone.isEmpty() || pass.isEmpty() || pass != confirm) {
                Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    val uid = it.user!!.uid
                    // Save profile: email + phoneNumber
                    val profile = mapOf(
                        "email" to email,
                        "phoneNumber" to phone
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        db.collection("users")
                            .document(uid)
                            .set(profile)
                            .await()
                    }
                    Toast.makeText(context, "Welcome, $email", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Sign-Up failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

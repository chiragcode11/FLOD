package com.example.mygaurdian

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mygaurdian.databinding.ActivityAuthBinding
import com.example.mygaurdian.presentation.auth.SignInFragment
import com.example.mygaurdian.presentation.auth.SignUpFragment
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSignUp.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignUpFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnSignIn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignInFragment())
                .addToBackStack(null)
                .commit()
        }

    }
}

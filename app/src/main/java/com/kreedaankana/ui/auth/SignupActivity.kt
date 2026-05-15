package com.kreedaankana.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kreedaankana.databinding.ActivitySignupBinding
import com.kreedaankana.ui.main.MainActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Entry Animation
        binding.inputLayout.alpha = 0f
        binding.inputLayout.translationY = 50f
        binding.inputLayout.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .start()

        binding.signupBtn.setOnClickListener {
            val emailText = binding.email.text.toString().trim()
            val passText = binding.password.text.toString().trim()

            if (emailText.isNotEmpty() && passText.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                binding.signupBtn.isEnabled = false

                auth.createUserWithEmailAndPassword(emailText, passText)
                    .addOnCompleteListener { task ->
                        binding.progressBar.visibility = View.GONE
                        binding.signupBtn.isEnabled = true
                        
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Profile Created Successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.goLogin.setOnClickListener {
            finish()
        }
    }
}

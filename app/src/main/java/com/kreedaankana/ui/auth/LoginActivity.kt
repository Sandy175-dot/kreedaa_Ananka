package com.kreedaankana.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kreedaankana.databinding.ActivityLoginBinding
import com.kreedaankana.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // 🔥 AUTO LOGIN
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Basic Entry Animation
        binding.inputLayout.alpha = 0f
        binding.inputLayout.translationY = 50f
        binding.inputLayout.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .start()

        binding.loginBtn.setOnClickListener {
            val emailText = binding.email.text.toString().trim()
            val passText = binding.password.text.toString().trim()

            if (emailText.isNotEmpty() && passText.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                binding.loginBtn.isEnabled = false
                
                auth.signInWithEmailAndPassword(emailText, passText)
                    .addOnCompleteListener { task ->
                        binding.progressBar.visibility = View.GONE
                        binding.loginBtn.isEnabled = true
                        
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Welcome to the Arena!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.goSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}

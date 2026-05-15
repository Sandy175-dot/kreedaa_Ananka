package com.kreedaankana.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kreedaankana.databinding.FragmentProfileBinding
import com.kreedaankana.ui.auth.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupProfile()

        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun setupProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email ?: "Unknown User"
            binding.profileEmail.text = email
            binding.profileInitial.text = email.take(1).uppercase()
            loadStats(currentUser.uid)
        }
    }

    private fun loadStats(userId: String) {
        // Bookings Count
        db.collection("bookings")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    binding.profileBookingsCount.text = value.size().toString()
                }
            }

        // Teams Count
        db.collection("teams")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    binding.profileTeamsCount.text = value.size().toString()
                }
            }

        // Match History Count
        db.collection("matches")
            .whereEqualTo("createdBy", userId)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    binding.profileMatchesCount.text = value.size().toString()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
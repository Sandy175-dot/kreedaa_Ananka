package com.kreedaankana.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kreedaankana.R
import com.kreedaankana.databinding.FragmentHomeBinding
import com.kreedaankana.ui.booking.BookingAdapter
import com.kreedaankana.ui.booking.BookingModel
import com.kreedaankana.ui.match.MatchHistoryFragment
import com.kreedaankana.ui.notification.NotificationViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val notificationViewModel: NotificationViewModel by viewModels()
    private lateinit var upcomingAdapter: BookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupDashboard()
        setupRecyclerView()
        observeNotifications()

        binding.swipeRefresh.setColorSchemeResources(R.color.brand_primary, R.color.brand_secondary)
        binding.swipeRefresh.setOnRefreshListener {
            loadStats()
            binding.swipeRefresh.isRefreshing = false
        }

        binding.notificationBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in_up, R.anim.fade_out, R.anim.fade_in_up, R.anim.fade_out)
                .replace(R.id.nav_host_fragment, NotificationFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.matchHistoryBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in_up, R.anim.fade_out, R.anim.fade_in_up, R.anim.fade_out)
                .replace(R.id.nav_host_fragment, MatchHistoryFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecyclerView() {
        upcomingAdapter = BookingAdapter(ArrayList())
        binding.upcomingRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }
    }

    private fun setupDashboard() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val name = currentUser.email?.split("@")?.get(0) ?: "Athlete"
            binding.emailText.text = name
            binding.avatarText.text = name.take(1).uppercase()
            loadStats()
            loadUpcomingMatches()
        }
    }

    private fun observeNotifications() {
        notificationViewModel.unreadCount.observe(viewLifecycleOwner) { count ->
            if (count > 0) {
                // Just a dot for "subtle" feel
                binding.unreadBadge.visibility = View.VISIBLE
            } else {
                binding.unreadBadge.visibility = View.GONE
            }
        }
    }

    private fun loadStats() {
        val userId = auth.currentUser?.uid ?: return

        // Booking Count
        db.collection("bookings")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    binding.totalBookingsText.text = value.size().toString()
                }
            }

        // Teams Stats
        db.collection("teams")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    var totalWins = 0
                    var totalMatches = 0
                    for (doc in value) {
                        val wins = doc.getLong("wins")?.toInt() ?: 0
                        val losses = doc.getLong("losses")?.toInt() ?: 0
                        totalWins += wins
                        totalMatches += (wins + losses)
                    }
                    
                    binding.matchCountText.text = totalMatches.toString()
                    
                    if (totalMatches > 0) {
                        val ratio = (totalWins.toFloat() / totalMatches.toFloat() * 100).toInt()
                        binding.winRatioText.text = "$ratio%"
                    } else {
                        binding.winRatioText.text = "0%"
                    }
                }
            }
    }

    private fun loadUpcomingMatches() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("bookings")
            .whereEqualTo("userId", userId)
            .limit(5)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    val list = value.documents.mapNotNull { doc ->
                        val sport = doc.getString("sport") ?: ""
                        val date = doc.getString("date") ?: ""
                        val time = doc.getString("slotTime") ?: ""
                        BookingModel(doc.id, sport, time, date, userId, null)
                    }
                    upcomingAdapter.updateList(list)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
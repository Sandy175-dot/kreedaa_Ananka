package com.kreedaankana.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kreedaankana.R
import com.kreedaankana.databinding.FragmentBookingsBinding
import com.kreedaankana.ui.booking.BookingActivity
import com.kreedaankana.ui.booking.BookingAdapter
import com.kreedaankana.ui.booking.BookingModel
import com.kreedaankana.ui.booking.BookingViewModel

class BookingFragment : Fragment() {

    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookingViewModel by viewModels()
    private lateinit var adapter: BookingAdapter

    private var currentSportFilter = "All"
    private var currentTimeFilter = "All"
    private val allBookings = ArrayList<BookingModel>()
    private val filteredList = ArrayList<BookingModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSpinners()
        setupObservers()

        binding.swipeRefresh.setColorSchemeResources(com.kreedaankana.R.color.neon_purple, com.kreedaankana.R.color.neon_cyan)
        binding.swipeRefresh.setOnRefreshListener {
            // Force refresh from ViewModel if needed, or just let observers handle it
            binding.swipeRefresh.isRefreshing = false
        }

        binding.addBookingFab.setOnClickListener {
            it.startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.button_click))
            startActivity(Intent(requireContext(), BookingActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = BookingAdapter(filteredList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupSpinners() {
        val sports = arrayOf("All", "Cricket", "Football", "Badminton")
        val times = arrayOf("All", "Morning", "Evening")

        val sportAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sports)
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sportSpinner.adapter = sportAdapter

        val timeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, times)
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.timeSpinner.adapter = timeAdapter

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentSportFilter = binding.sportSpinner.selectedItem.toString()
                currentTimeFilter = binding.timeSpinner.selectedItem.toString()
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.sportSpinner.onItemSelectedListener = listener
        binding.timeSpinner.onItemSelectedListener = listener
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading && allBookings.isEmpty()) {
                binding.shimmerView.visibility = View.VISIBLE
                binding.shimmerView.startShimmer()
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.shimmerView.stopShimmer()
                binding.shimmerView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.bookings.observe(viewLifecycleOwner) { list ->
            allBookings.clear()
            allBookings.addAll(list)
            applyFilters()
        }
    }

    private fun applyFilters() {
        filteredList.clear()
        for (booking in allBookings) {
            val matchesSport = currentSportFilter == "All" || booking.sport.equals(currentSportFilter, ignoreCase = true)
            val matchesTime = currentTimeFilter == "All" || booking.time.equals(currentTimeFilter, ignoreCase = true)
            if (matchesSport && matchesTime) {
                filteredList.add(booking)
            }
        }
        adapter.notifyDataSetChanged()
        
        if (filteredList.isEmpty() && !viewModel.isLoading.value!!) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
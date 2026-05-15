package com.kreedaankana.ui.booking

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.kreedaankana.databinding.ActivityBookingBinding
import java.text.SimpleDateFormat
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private val viewModel: BookingViewModel by viewModels()
    private lateinit var slotAdapter: SlotAdapter
    private val slotsList = ArrayList<SlotModel>()
    
    private var selectedDate: String = ""
    private var selectedSlot: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSportSpinner()
        setupCalendar()
        setupSlotsGrid()
        setupObservers()

        binding.bookBtn.setOnClickListener {
            val sport = binding.sportInput.text.toString().trim()
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedSlot.isEmpty()) {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (sport.isEmpty() || sport == "Pick your sport") {
                Toast.makeText(this, "Please select a sport", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.createBooking(sport, selectedSlot, selectedDate)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSportSpinner() {
        val sports = listOf("Cricket", "Football", "Badminton", "Tennis", "Basketball")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, sports)
        binding.sportInput.setAdapter(adapter)
    }

    private fun setupCalendar() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = sdf.format(Date(binding.calendarView.date))

        binding.calendarView.minDate = System.currentTimeMillis() - 1000
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = sdf.format(calendar.time)
            
            // Clear previous selection
            selectedSlot = ""
            viewModel.observeBookedSlots(selectedDate)
        }
        
        // Initial fetch
        viewModel.observeBookedSlots(selectedDate)
    }

    private fun setupSlotsGrid() {
        val times = listOf(
            "06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM",
            "10:00 AM", "11:00 AM", "12:00 PM", "01:00 PM",
            "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM",
            "06:00 PM", "07:00 PM", "08:00 PM", "09:00 PM"
        )
        
        times.forEach { slotsList.add(SlotModel(it)) }

        slotAdapter = SlotAdapter(slotsList) { clickedSlot ->
            slotsList.forEach { it.isSelected = it.time == clickedSlot.time }
            selectedSlot = clickedSlot.time
            slotAdapter.notifyDataSetChanged()
        }

        binding.slotsRecyclerView.apply {
            layoutManager = GridLayoutManager(this@BookingActivity, 3)
            adapter = slotAdapter
        }
    }

    private fun setupObservers() {
        viewModel.bookedSlots.observe(this) { bookedTimes ->
            slotsList.forEach { slot ->
                slot.isBooked = bookedTimes.contains(slot.time)
                // If the currently selected slot is now booked, clear it
                if (slot.isBooked && selectedSlot == slot.time) {
                    selectedSlot = ""
                }
            }
            slotAdapter.notifyDataSetChanged()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.bookBtn.isEnabled = !isLoading
        }

        viewModel.createStatus.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Booking Successful!", Toast.LENGTH_LONG).show()
                finish()
            }.onFailure { exception ->
                Toast.makeText(this, exception.message ?: "Failed to book", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

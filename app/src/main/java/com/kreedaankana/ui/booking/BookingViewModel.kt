package com.kreedaankana.ui.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {
    private val repository = BookingRepository()
    private var bookedSlotsJob: Job? = null

    private val _bookings = MutableLiveData<List<BookingModel>>()
    val bookings: LiveData<List<BookingModel>> = _bookings

    private val _bookedSlots = MutableLiveData<List<String>>()
    val bookedSlots: LiveData<List<String>> = _bookedSlots

    private val _createStatus = MutableLiveData<Result<Boolean>>()
    val createStatus: LiveData<Result<Boolean>> = _createStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        observeBookings()
    }

    fun observeBookedSlots(date: String) {
        bookedSlotsJob?.cancel()
        bookedSlotsJob = viewModelScope.launch {
            repository.getBookedSlotsForDate(date).collect {
                _bookedSlots.value = it
            }
        }
    }

    private fun observeBookings() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getBookingsRealtime()
                .catch { _isLoading.value = false }
                .collect { list ->
                    _bookings.value = list
                    _isLoading.value = false
                }
        }
    }

    fun createBooking(sport: String, slotTime: String, date: String) {
        if (sport.isEmpty() || slotTime.isEmpty() || date.isEmpty()) {
            _createStatus.value = Result.failure(Exception("Please select date, sport and slot"))
            return
        }
        
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.createBooking(sport, slotTime, date)
            _createStatus.value = result
            _isLoading.value = false
        }
    }
}

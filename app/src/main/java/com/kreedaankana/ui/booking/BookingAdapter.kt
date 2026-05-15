package com.kreedaankana.ui.booking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kreedaankana.databinding.ItemBookingBinding

class BookingAdapter(private val bookingList: ArrayList<BookingModel>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(val binding: ItemBookingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]
        holder.binding.sportText.text = booking.sport
        holder.binding.dateText.text = booking.date
        holder.binding.timeText.text = booking.time
        
        holder.binding.statusChip.text = "CONFIRMED"
        
        holder.binding.sportEmojiText.text = when (booking.sport.lowercase()) {
            "cricket" -> "🏏"
            "football" -> "⚽"
            "badminton" -> "🏸"
            else -> "🏟️"
        }
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    fun updateList(newList: List<BookingModel>) {
        bookingList.clear()
        bookingList.addAll(newList)
        notifyDataSetChanged()
    }
}

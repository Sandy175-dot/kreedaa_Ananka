package com.kreedaankana.ui.notification

import android.graphics.Color
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kreedaankana.databinding.ItemNotificationBinding

class NotificationAdapter(
    private var notificationList: List<NotificationModel>,
    private val onItemClick: (NotificationModel) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        with(holder.binding) {
            titleText.text = notification.title
            messageText.text = notification.message
            
            val time = notification.timestamp?.toDate()?.time ?: System.currentTimeMillis()
            timeText.text = DateUtils.getRelativeTimeSpanString(time)

            if (notification.isRead) {
                notificationCard.setCardBackgroundColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#1E1E2E")))
                notificationCard.strokeColor = Color.parseColor("#333344")
                notificationCard.cardElevation = 0f
            } else {
                notificationCard.setCardBackgroundColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#2A2A3D")))
                notificationCard.strokeColor = Color.parseColor("#8B5CF6")
                notificationCard.cardElevation = 8f
            }

            root.setOnClickListener { onItemClick(notification) }
        }
    }

    override fun getItemCount(): Int = notificationList.size

    fun updateList(newList: List<NotificationModel>) {
        notificationList = newList
        notifyDataSetChanged()
    }
}

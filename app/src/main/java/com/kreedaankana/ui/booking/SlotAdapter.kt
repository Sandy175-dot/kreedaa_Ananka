package com.kreedaankana.ui.booking

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kreedaankana.databinding.ItemSlotBinding

class SlotAdapter(
    private val slots: List<SlotModel>,
    private val onSlotSelected: (SlotModel) -> Unit
) : RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    class SlotViewHolder(val binding: ItemSlotBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val binding = ItemSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]
        holder.binding.slotTimeText.text = slot.time

        when {
            slot.isBooked -> {
                holder.binding.slotCard.isEnabled = false
                holder.binding.slotCard.alpha = 0.4f
                holder.binding.slotCard.strokeColor = Color.parseColor("#FF5252") // Modern Red
                holder.binding.slotCard.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#2D1A1A")))
                holder.binding.slotTimeText.setTextColor(Color.parseColor("#66FFFFFF"))
            }
            slot.isSelected -> {
                holder.binding.slotCard.isEnabled = true
                holder.binding.slotCard.alpha = 1.0f
                holder.binding.slotCard.strokeColor = Color.parseColor("#A855F7") // Neon Purple
                holder.binding.slotCard.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#2D1B4E")))
                holder.binding.slotTimeText.setTextColor(Color.WHITE)
                
                // Animated Scale for Selected
                holder.itemView.scaleX = 1.05f
                holder.itemView.scaleY = 1.05f
                holder.binding.slotCard.cardElevation = 12f
            }
            else -> {
                holder.binding.slotCard.isEnabled = true
                holder.binding.slotCard.alpha = 1.0f
                holder.binding.slotCard.strokeColor = Color.parseColor("#22D3EE") // Neon Cyan
                holder.binding.slotCard.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#1E293B"))) // Dark Slate
                holder.binding.slotTimeText.setTextColor(Color.parseColor("#E2E8F0"))
                
                holder.itemView.scaleX = 1.0f
                holder.itemView.scaleY = 1.0f
                holder.binding.slotCard.cardElevation = 2f
            }
        }

        holder.binding.root.setOnClickListener {
            if (!slot.isBooked) {
                // Simple scale animation on tap
                holder.itemView.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                    holder.itemView.animate().scaleX(1.05f).scaleY(1.05f).setDuration(100).start()
                    onSlotSelected(slot)
                }.start()
            }
        }
    }

    override fun getItemCount(): Int = slots.size
}

package com.kreedaankana.ui.challenge

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kreedaankana.databinding.ItemChallengeBinding

class ChallengeAdapter(
    private var challengeList: List<ChallengeModel>,
    private val currentUserId: String,
    private val onAccept: (ChallengeModel) -> Unit,
    private val onReject: (ChallengeModel) -> Unit
) : RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder>() {

    class ChallengeViewHolder(val binding: ItemChallengeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val binding = ItemChallengeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChallengeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        val challenge = challengeList[position]
        with(holder.binding) {
            sportText.text = challenge.sport
            teamsText.text = "${challenge.challengerTeam} vs ${challenge.opponentTeam}"
            dateText.text = "📅 ${challenge.matchDate}"
            venueText.text = "📍 ${challenge.venue}"
            messageText.text = challenge.message
            statusBadge.text = challenge.status

            // Status Badge Color
            when (challenge.status) {
                "Accepted" -> {
                    statusBadge.setTextColor(Color.WHITE)
                    statusBadge.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#22C55E"))
                }
                "Rejected" -> {
                    statusBadge.setTextColor(Color.WHITE)
                    statusBadge.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#EF4444"))
                }
                else -> {
                    statusBadge.setTextColor(Color.WHITE)
                    statusBadge.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#8B5CF6"))
                }
            }

            // Show actions only for the opponent user and if status is Pending
            if (challenge.opponentUserId == currentUserId && challenge.status == "Pending") {
                actionLayout.visibility = View.VISIBLE
            } else {
                actionLayout.visibility = View.GONE
            }

            acceptBtn.setOnClickListener { onAccept(challenge) }
            rejectBtn.setOnClickListener { onReject(challenge) }
        }
    }

    override fun getItemCount(): Int = challengeList.size

    fun updateList(newList: List<ChallengeModel>) {
        challengeList = newList
        notifyDataSetChanged()
    }
}

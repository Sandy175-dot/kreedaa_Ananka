package com.kreedaankana.ui.leaderboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kreedaankana.databinding.ItemLeaderboardBinding

class LeaderboardAdapter(private val leaderboardList: ArrayList<LeaderboardModel>) :
    RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    class LeaderboardViewHolder(val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val entry = leaderboardList[position]
        val rank = position + 1
        
        with(holder.binding) {
            rankText.text = rank.toString()
            teamNameText.text = entry.teamName
            captainText.text = "Captain: ${entry.captain}"
            statsText.text = "W: ${entry.wins} | D: ${entry.draws} | L: ${entry.losses}"
            pointsText.text = entry.points.toString()

            // Highlight top 3
            if (rank <= 3) {
                rankText.setTextColor(Color.parseColor("#FFD700")) // Gold
                root.strokeColor = Color.parseColor("#FFD700")
                root.strokeWidth = 2
            } else {
                rankText.setTextColor(Color.parseColor("#8B5CF6")) // Purple
                root.strokeColor = Color.parseColor("#333344")
                root.strokeWidth = 1
            }
        }
    }

    override fun getItemCount(): Int = leaderboardList.size
}

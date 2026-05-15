package com.kreedaankana.ui.team

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kreedaankana.databinding.ItemTeamBinding
import java.util.Locale

class TeamAdapter(
    private var teamList: List<TeamModel>,
    private val onItemClick: (TeamModel) -> Unit
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    private var fullList: List<TeamModel> = teamList

    class TeamViewHolder(val binding: ItemTeamBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = teamList[position]
        with(holder.binding) {
            teamNameText.text = team.teamName
            captainText.text = "Captain: ${team.captain}"
            playersCountText.text = team.playersCount.toString()
            winsText.text = team.wins.toString()
            lossesText.text = team.losses.toString()
            
            root.setOnClickListener { onItemClick(team) }
        }
    }

    override fun getItemCount(): Int = teamList.size

    fun updateList(newList: List<TeamModel>) {
        fullList = newList
        teamList = newList
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        teamList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter {
                it.teamName.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) ||
                it.captain.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
            }
        }
        notifyDataSetChanged()
    }
}

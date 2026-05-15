package com.kreedaankana.ui.team

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.kreedaankana.databinding.ItemPlayerBinding

class PlayerAdapter(
    private var players: List<PlayerModel>,
    private val onEdit: (PlayerModel) -> Unit,
    private val onDelete: (PlayerModel) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    class PlayerViewHolder(val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.binding.playerNameText.text = player.playerName
        holder.binding.jerseyNumberText.text = player.jerseyNumber
        holder.binding.roleChip.text = player.role
        holder.binding.statsText.text = "${player.runs} Runs | ${player.wickets} Wkts"
        holder.binding.matchesText.text = "${player.matchesPlayed} Matches"

        holder.binding.optionsBtn.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menu.add("Edit")
            popup.menu.add("Delete")
            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Edit" -> onEdit(player)
                    "Delete" -> onDelete(player)
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = players.size

    fun updateList(newList: List<PlayerModel>) {
        players = newList
        notifyDataSetChanged()
    }
}

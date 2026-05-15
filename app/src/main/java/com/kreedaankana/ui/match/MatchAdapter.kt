package com.kreedaankana.ui.match

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kreedaankana.databinding.ItemMatchBinding
import java.util.Locale

class MatchAdapter(private var matchList: List<MatchModel>) :
    RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    private var fullList: List<MatchModel> = matchList

    class MatchViewHolder(val binding: ItemMatchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matchList[position]
        with(holder.binding) {
            sportText.text = match.sport
            dateText.text = "${match.matchDate} | ${match.matchTime}"
            teamAText.text = match.teamA
            teamBText.text = match.teamB
            scoreText.text = "${match.scoreA} - ${match.scoreB}"
            winnerNameText.text = match.winner
            resultText.text = match.result
        }
    }

    override fun getItemCount(): Int = matchList.size

    fun updateList(newList: List<MatchModel>) {
        fullList = newList
        matchList = newList
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        matchList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter {
                it.teamA.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) ||
                it.teamB.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) ||
                it.sport.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) ||
                it.winner.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
            }
        }
        notifyDataSetChanged()
    }
}

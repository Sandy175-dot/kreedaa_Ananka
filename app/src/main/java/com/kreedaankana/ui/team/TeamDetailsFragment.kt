package com.kreedaankana.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kreedaankana.databinding.FragmentTeamDetailsBinding

class TeamDetailsFragment : Fragment() {

    private var _binding: FragmentTeamDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TeamViewModel by activityViewModels()
    private lateinit var adapter: PlayerAdapter
    private var teamId: String = ""
    private var teamName: String = ""

    companion object {
        fun newInstance(teamId: String, teamName: String): TeamDetailsFragment {
            val fragment = TeamDetailsFragment()
            val args = Bundle()
            args.putString("teamId", teamId)
            args.putString("teamName", teamName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teamId = arguments?.getString("teamId") ?: ""
        teamName = arguments?.getString("teamName") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        binding.addPlayerFab.setOnClickListener {
            AddPlayerBottomSheetFragment(teamId).show(childFragmentManager, "add_player")
        }

        viewModel.fetchPlayers(teamId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.teamNameTitle.text = teamName
        binding.teamAvatarText.text = teamName.take(1).uppercase()
    }

    private fun setupRecyclerView() {
        adapter = PlayerAdapter(emptyList(), onEdit = { player ->
            AddPlayerBottomSheetFragment(teamId, player).show(childFragmentManager, "edit_player")
        }, onDelete = { player ->
            showDeleteConfirmation(player)
        })
        binding.playerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playerRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.teams.observe(viewLifecycleOwner) { teams ->
            val team = teams.find { it.teamId == teamId }
            if (team != null) {
                updateTeamUI(team)
            }
        }

        viewModel.players.observe(viewLifecycleOwner) { players ->
            adapter.updateList(players)
        }
    }

    private fun updateTeamUI(team: TeamModel) {
        binding.captainText.text = "Captain: ${team.captain}"
        val total = team.wins + team.losses + team.draw
        binding.matchesPlayedText.text = total.toString()
        val winRate = if (total > 0) (team.wins * 100 / total) else 0
        binding.winRateText.text = "$winRate%"
        
        binding.avgScoreText.text = team.avgScore.toString()
        binding.recentFormText.text = team.recentForm
        binding.playerCountTitle.text = team.playersCount.toString()
    }

    private fun showDeleteConfirmation(player: PlayerModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Player")
            .setMessage("Are you sure you want to delete ${player.playerName}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deletePlayer(teamId, player.playerId) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Player deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

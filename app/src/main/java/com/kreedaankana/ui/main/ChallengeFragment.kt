package com.kreedaankana.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.kreedaankana.R
import com.kreedaankana.databinding.FragmentChallengesBinding
import com.kreedaankana.ui.challenge.ChallengeAdapter
import com.kreedaankana.ui.challenge.ChallengeModel
import com.kreedaankana.ui.challenge.ChallengeViewModel

class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChallengeViewModel by viewModels()
    private lateinit var adapter: ChallengeAdapter
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChallengesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.swipeRefresh.setColorSchemeResources(com.kreedaankana.R.color.neon_purple, com.kreedaankana.R.color.neon_cyan)
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
        }

        binding.addChallengeFab.setOnClickListener {
            it.startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), com.kreedaankana.R.anim.button_click))
            showCreateChallengeDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = ChallengeAdapter(emptyList(), currentUserId,
            onAccept = { challenge ->
                viewModel.updateChallengeStatus(challenge.challengeId, "Accepted", challenge.createdBy, challenge.challengerTeam, challenge.opponentTeam)
            },
            onReject = { challenge ->
                viewModel.updateChallengeStatus(challenge.challengeId, "Rejected", challenge.createdBy, challenge.challengerTeam, challenge.opponentTeam)
            }
        )
        binding.challengeRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading && (viewModel.challenges.value ?: emptyList()).isEmpty()) {
                binding.shimmerView.visibility = View.VISIBLE
                binding.shimmerView.startShimmer()
                binding.challengeRecyclerView.visibility = View.GONE
            } else {
                binding.shimmerView.stopShimmer()
                binding.shimmerView.visibility = View.GONE
                binding.challengeRecyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.challenges.observe(viewLifecycleOwner) { challenges ->
            if (challenges.isEmpty() && !viewModel.isLoading.value!!) {
                binding.emptyStateText.visibility = View.VISIBLE
            } else {
                binding.emptyStateText.visibility = View.GONE
            }
            adapter.updateList(challenges)
        }
    }

    private fun showCreateChallengeDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_challenge, null)
        val challengerSpinner = dialogView.findViewById<Spinner>(R.id.challengerSpinner)
        val opponentSpinner = dialogView.findViewById<Spinner>(R.id.opponentSpinner)
        val sportEdit = dialogView.findViewById<EditText>(R.id.sportEdit)
        val dateEdit = dialogView.findViewById<EditText>(R.id.dateEdit)
        val venueEdit = dialogView.findViewById<EditText>(R.id.venueEdit)
        val messageEdit = dialogView.findViewById<EditText>(R.id.messageEdit)

        val allTeams = viewModel.teams.value ?: emptyList()
        val myTeams = allTeams.filter { it.userId == currentUserId }
        val otherTeams = allTeams.filter { it.userId != currentUserId }

        if (myTeams.isEmpty()) {
            Toast.makeText(requireContext(), "You need a team to post a challenge!", Toast.LENGTH_SHORT).show()
            return
        }

        val myTeamNames = myTeams.map { it.teamName }
        val otherTeamNames = otherTeams.map { it.teamName }

        challengerSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, myTeamNames)
        opponentSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, otherTeamNames)

        AlertDialog.Builder(requireContext())
            .setTitle("Post Match Challenge")
            .setView(dialogView)
            .setPositiveButton("Post") { _, _ ->
                val challengerIndex = challengerSpinner.selectedItemPosition
                val opponentIndex = opponentSpinner.selectedItemPosition
                
                if (challengerIndex >= 0 && opponentIndex >= 0) {
                    val challenger = myTeams[challengerIndex]
                    val opponent = otherTeams[opponentIndex]
                    val sport = sportEdit.text.toString().trim()
                    val date = dateEdit.text.toString().trim()
                    val venue = venueEdit.text.toString().trim()
                    val message = messageEdit.text.toString().trim()

                    if (sport.isNotEmpty() && date.isNotEmpty() && venue.isNotEmpty()) {
                        val challenge = ChallengeModel(
                            challengerTeam = challenger.teamName,
                            opponentTeam = opponent.teamName,
                            opponentUserId = opponent.userId,
                            sport = sport,
                            matchDate = date,
                            venue = venue,
                            message = message
                        )
                        viewModel.createChallenge(challenge) { success ->
                            if (success) {
                                Toast.makeText(requireContext(), "Challenge Posted!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Failed to post challenge", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Please fill required fields", Toast.LENGTH_SHORT).show()
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
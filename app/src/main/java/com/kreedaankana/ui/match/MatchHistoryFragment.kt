package com.kreedaankana.ui.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kreedaankana.R
import com.kreedaankana.databinding.FragmentMatchHistoryBinding

class MatchHistoryFragment : Fragment() {

    private var _binding: FragmentMatchHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchViewModel by viewModels()
    private lateinit var adapter: MatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupSearch()
        setupRefresh()

        binding.addMatchFab.setOnClickListener {
            it.startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.button_click))
            showRecordMatchDialog()
        }

        binding.toolbar.setNavigationOnClickListener { 
            activity?.onBackPressed()
        }
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.neon_purple, R.color.neon_cyan)
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupSearch() {
        binding.matchSearchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = MatchAdapter(emptyList())
        binding.matchRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading && (viewModel.matches.value ?: emptyList()).isEmpty()) {
                binding.shimmerView.visibility = View.VISIBLE
                binding.shimmerView.startShimmer()
                binding.matchRecyclerView.visibility = View.GONE
            } else {
                binding.shimmerView.stopShimmer()
                binding.shimmerView.visibility = View.GONE
                binding.matchRecyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.matches.observe(viewLifecycleOwner) { matches ->
            if (matches.isEmpty() && !viewModel.isLoading.value!!) {
                binding.emptyStateText.visibility = View.VISIBLE
            } else {
                binding.emptyStateText.visibility = View.GONE
            }
            adapter.updateList(matches)
        }
    }

    private fun showRecordMatchDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_record_match, null)
        val sportEdit = dialogView.findViewById<EditText>(R.id.sportEdit)
        val teamAEdit = dialogView.findViewById<EditText>(R.id.teamAEdit)
        val teamBEdit = dialogView.findViewById<EditText>(R.id.teamBEdit)
        val scoreAEdit = dialogView.findViewById<EditText>(R.id.scoreAEdit)
        val scoreBEdit = dialogView.findViewById<EditText>(R.id.scoreBEdit)
        val dateEdit = dialogView.findViewById<EditText>(R.id.dateEdit)
        val timeEdit = dialogView.findViewById<EditText>(R.id.timeEdit)

        AlertDialog.Builder(requireContext())
            .setTitle("Record Match Result")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val sport = sportEdit.text.toString().trim()
                val teamA = teamAEdit.text.toString().trim()
                val teamB = teamBEdit.text.toString().trim()
                val scoreA = scoreAEdit.text.toString().toIntOrNull() ?: 0
                val scoreB = scoreBEdit.text.toString().toIntOrNull() ?: 0
                val date = dateEdit.text.toString().trim()
                val time = timeEdit.text.toString().trim()

                if (sport.isNotEmpty() && teamA.isNotEmpty() && teamB.isNotEmpty() && date.isNotEmpty()) {
                    val newMatch = MatchModel(
                        sport = sport,
                        teamA = teamA,
                        teamB = teamB,
                        scoreA = scoreA,
                        scoreB = scoreB,
                        matchDate = date,
                        matchTime = time
                    )
                    viewModel.saveMatch(newMatch) { success ->
                        if (success) {
                            Toast.makeText(requireContext(), "Match recorded & stats updated!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Failed to record match", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Please fill required fields", Toast.LENGTH_SHORT).show()
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
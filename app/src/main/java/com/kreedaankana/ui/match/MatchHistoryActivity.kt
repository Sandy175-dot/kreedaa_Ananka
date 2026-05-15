package com.kreedaankana.ui.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kreedaankana.R
import com.kreedaankana.databinding.ActivityMatchHistoryBinding

class MatchHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatchHistoryBinding
    private val viewModel: MatchViewModel by viewModels()
    private lateinit var adapter: MatchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
        setupSearch()

        binding.addMatchFab.setOnClickListener {
            showRecordMatchDialog()
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
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
        viewModel.matches.observe(this) { matches ->
            if (matches.isEmpty()) {
                binding.emptyStateText.visibility = View.VISIBLE
            } else {
                binding.emptyStateText.visibility = View.GONE
            }
            adapter.updateList(matches)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showRecordMatchDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_record_match, null)
        val sportEdit = dialogView.findViewById<EditText>(R.id.sportEdit)
        val teamAEdit = dialogView.findViewById<EditText>(R.id.teamAEdit)
        val teamBEdit = dialogView.findViewById<EditText>(R.id.teamBEdit)
        val scoreAEdit = dialogView.findViewById<EditText>(R.id.scoreAEdit)
        val scoreBEdit = dialogView.findViewById<EditText>(R.id.scoreBEdit)
        val dateEdit = dialogView.findViewById<EditText>(R.id.dateEdit)
        val timeEdit = dialogView.findViewById<EditText>(R.id.timeEdit)

        AlertDialog.Builder(this)
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
                            Toast.makeText(this, "Match recorded & stats updated!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to record match", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

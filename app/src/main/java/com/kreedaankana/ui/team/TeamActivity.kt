package com.kreedaankana.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.kreedaankana.R
import com.kreedaankana.databinding.ActivityTeamBinding

class TeamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamBinding
    private val viewModel: TeamViewModel by viewModels()
    private lateinit var adapter: TeamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
        setupSearch()

        binding.addTeamFab.setOnClickListener {
            it.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.button_click))
            showCreateTeamDialog()
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSearch() {
        binding.teamSearchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
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
        adapter = TeamAdapter(emptyList()) { team ->
            openTeamDetails(team)
        }
        binding.teamRecyclerView.adapter = adapter
    }

    private fun openTeamDetails(team: TeamModel) {
        val fragment = TeamDetailsFragment.newInstance(team.teamId, team.teamName)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in_up, R.anim.fade_out, R.anim.fade_in_up, R.anim.fade_out)
            .add(R.id.teamFragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun observeViewModel() {
        viewModel.teams.observe(this) { teams ->
            if (teams.isEmpty()) {
                binding.emptyStateText.visibility = View.VISIBLE
            } else {
                binding.emptyStateText.visibility = View.GONE
            }
            adapter.updateList(teams)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showCreateTeamDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_team, null)
        val teamNameEdit = dialogView.findViewById<EditText>(R.id.teamNameEdit)
        val captainEdit = dialogView.findViewById<EditText>(R.id.captainEdit)
        val playersEdit = dialogView.findViewById<EditText>(R.id.playersEdit)

        AlertDialog.Builder(this)
            .setTitle("Create New Team")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name = teamNameEdit.text.toString().trim()
                val captain = captainEdit.text.toString().trim()
                val players = playersEdit.text.toString().toIntOrNull() ?: 0

                if (name.isNotEmpty() && captain.isNotEmpty() && players > 0) {
                    val newTeam = TeamModel(
                        teamName = name,
                        captain = captain,
                        playersCount = players,
                        timestamp = Timestamp.now()
                    )
                    viewModel.createTeam(newTeam) { success ->
                        if (success) {
                            Toast.makeText(this, "Team Created!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to create team", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

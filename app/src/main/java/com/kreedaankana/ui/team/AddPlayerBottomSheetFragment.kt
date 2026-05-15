package com.kreedaankana.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.kreedaankana.databinding.DialogAddPlayerBinding

class AddPlayerBottomSheetFragment(
    private val teamId: String,
    private val playerToEdit: PlayerModel? = null
) : BottomSheetDialogFragment() {

    private var _binding: DialogAddPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TeamViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRoleDropdown()

        if (playerToEdit != null) {
            binding.dialogTitle.text = "EDIT PLAYER"
            binding.playerNameEdit.setText(playerToEdit.playerName)
            binding.roleDropdown.setText(playerToEdit.role, false)
            binding.jerseyEdit.setText(playerToEdit.jerseyNumber)
            binding.matchesEdit.setText(playerToEdit.matchesPlayed.toString())
            binding.runsEdit.setText(playerToEdit.runs.toString())
            binding.wicketsEdit.setText(playerToEdit.wickets.toString())
            binding.saveBtn.text = "UPDATE PLAYER"
        }

        binding.saveBtn.setOnClickListener {
            savePlayer()
        }
    }

    private fun setupRoleDropdown() {
        val roles = arrayOf("Batsman", "Bowler", "All-Rounder", "Wicket Keeper")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, roles)
        binding.roleDropdown.setAdapter(adapter)
    }

    private fun savePlayer() {
        val name = binding.playerNameEdit.text.toString().trim()
        val role = binding.roleDropdown.text.toString().trim()
        val jersey = binding.jerseyEdit.text.toString().trim()
        val matches = binding.matchesEdit.text.toString().toIntOrNull() ?: 0
        val runs = binding.runsEdit.text.toString().toIntOrNull() ?: 0
        val wickets = binding.wicketsEdit.text.toString().toIntOrNull() ?: 0

        if (name.isEmpty() || role.isEmpty() || jersey.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill name, role and jersey", Toast.LENGTH_SHORT).show()
            return
        }

        val player = (playerToEdit ?: PlayerModel()).copy(
            playerName = name,
            role = role,
            jerseyNumber = jersey,
            matchesPlayed = matches,
            runs = runs,
            wickets = wickets,
            timestamp = Timestamp.now()
        )

        if (playerToEdit == null) {
            viewModel.addPlayer(teamId, player) { success ->
                if (success) {
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Failed to add player", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            viewModel.updatePlayer(teamId, player) { success ->
                if (success) {
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Failed to update player", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

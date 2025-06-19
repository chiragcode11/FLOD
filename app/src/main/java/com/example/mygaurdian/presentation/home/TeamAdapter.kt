package com.example.mygaurdian.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mygaurdian.databinding.ItemTeamBinding
import com.example.mygaurdian.domain.model.Team

class TeamAdapter(
    private var teams: List<Team>,
    private val onShareToggled: (Team, Boolean) -> Unit,
    private val onTeamClick: (Team) -> Unit      // ← new
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    inner class TeamViewHolder(val binding: ItemTeamBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(team: Team) {
            binding.tvTeamName.text = team.name
            binding.switchShare.isChecked = team.sharing

            binding.switchShare.setOnCheckedChangeListener { _, isChecked ->
                onShareToggled(team, isChecked)
            }

            binding.root.setOnClickListener {
                onTeamClick(team)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TeamViewHolder(
            ItemTeamBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) =
        holder.bind(teams[position])

    override fun getItemCount() = teams.size

    fun updateData(newTeams: List<Team>) {
        teams = newTeams
        notifyDataSetChanged()
    }
}

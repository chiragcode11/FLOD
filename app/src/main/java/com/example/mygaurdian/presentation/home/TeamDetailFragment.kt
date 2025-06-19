package com.example.mygaurdian.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygaurdian.R
import com.example.mygaurdian.data.local.ContactLocalDataSource
import com.example.mygaurdian.data.remote.TeamRemoteDataSource
import com.example.mygaurdian.data.repository.TeamRepositoryImpl
import com.example.mygaurdian.databinding.FragmentTeamDetailBinding
import com.example.mygaurdian.domain.model.User
import com.example.mygaurdian.domain.usecases.DeleteTeamUseCase
import com.example.mygaurdian.domain.usecases.GetTeamMembersUseCase
import com.example.mygaurdian.domain.usecases.InviteMemberUseCase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class TeamDetailFragment : Fragment(R.layout.fragment_team_detail) {
    private var _b: FragmentTeamDetailBinding? = null
    private val b get() = _b!!

    private val args by lazy { requireArguments() }
    private val teamId by lazy { args.getString("teamId")!! }
    private val teamName by lazy { args.getString("teamName")!! }

    private val vm by viewModels<TeamDetailViewModel> {
        val ds = TeamRemoteDataSource(FirebaseFirestore.getInstance())
        val repo = TeamRepositoryImpl(ds)
        TeamDetailViewModelFactory(
            GetTeamMembersUseCase(repo),
            DeleteTeamUseCase(repo),
            InviteMemberUseCase(repo)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentTeamDetailBinding.bind(view)
        b.toolbar.title = teamName

        val adapter = MemberAdapter(emptyList(),
            onRemove = { /* … */ },
            onInvite = { user -> vm.onInvite(teamId, user.toString(), teamName) }
        )
        b.recyclerMembers.layoutManager = LinearLayoutManager(context)
        b.recyclerMembers.adapter = adapter

        lifecycleScope.launch {
            val ds = TeamRemoteDataSource(FirebaseFirestore.getInstance())
            val repo = TeamRepositoryImpl(ds)

            val regDeferred = async { vm.getMembers(teamId) }

            val contactIds = async { ds.getTeamContactIds(teamId) }

            val registered: List<User> = regDeferred.await()
            val localIds: List<String>    = contactIds.await()
            val allContacts = ContactLocalDataSource(requireContext().contentResolver)
                .fetchContacts()
            val localUsers = allContacts
                .filter { localIds.contains(it.id) }
                .map { User(id = it.id, email = it.name, phoneNumber = it.phone) }

            val combined = registered + localUsers
            adapter.update(combined)
        }

        b.btnDeleteTeam.setOnClickListener {
            vm.onDelete(teamId) {
                findNavController().popBackStack(R.id.homeFragment, false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}

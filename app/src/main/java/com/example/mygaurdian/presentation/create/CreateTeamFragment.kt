package com.example.mygaurdian.presentation.create

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygaurdian.data.local.ContactLocalDataSource
import com.example.mygaurdian.data.remote.TeamRemoteDataSource
import com.example.mygaurdian.data.repository.ContactRepositoryImpl
import com.example.mygaurdian.data.repository.TeamRepositoryImpl
import com.example.mygaurdian.databinding.FragmentCreateTeamBinding
import com.example.mygaurdian.domain.usecases.AddTeamUseCase
import com.example.mygaurdian.domain.usecases.GetContactsUseCase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collect

class CreateTeamFragment : Fragment() {

    companion object {
        private const val REQUEST_READ_CONTACTS = 1002
    }

    private var _binding: FragmentCreateTeamBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CreateTeamViewModel
    private lateinit var adapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCreateTeamBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        } else {
            initUiAndViewModel()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                initUiAndViewModel()
            } else {
                Toast.makeText(
                    context,
                    "Contacts permission is required to create a team.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initUiAndViewModel() {
        adapter = ContactAdapter(emptyList()) { contact ->
            viewModel.toggleContact(contact)
        }
        binding.recyclerContacts.layoutManager = LinearLayoutManager(context)
        binding.recyclerContacts.adapter = adapter

        val contactDs = ContactLocalDataSource(requireContext().contentResolver)
        val contactRepo = ContactRepositoryImpl(contactDs)
        val teamDs = TeamRemoteDataSource(FirebaseFirestore.getInstance())
        val teamRepo = TeamRepositoryImpl(teamDs)
        val factory = CreateTeamViewModelFactory(
            GetContactsUseCase(contactRepo),
            AddTeamUseCase(teamRepo)
        )
        viewModel = ViewModelProvider(this, factory)
            .get(CreateTeamViewModel::class.java)

        lifecycleScope.launchWhenStarted {
            viewModel.contacts.collect { list ->
                adapter.update(list)
            }
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etTeamName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(context, "Enter team name", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.saveTeam(name)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.isSaving.collect { saving ->
                binding.progress.visibility = if (saving) View.VISIBLE else View.GONE
                if (!saving && binding.etTeamName.text.isNotBlank()) {
                    Toast.makeText(context, "Team created!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.mygaurdian.presentation.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygaurdian.R
import com.example.mygaurdian.data.remote.SOSRemoteDataSource
import com.example.mygaurdian.data.repository.SOSRepositoryImpl
import com.example.mygaurdian.databinding.FragmentDashboardBinding
import com.example.mygaurdian.domain.usecases.GetSOSHistoryUseCase
import com.example.mygaurdian.domain.usecases.GetTeamsUseCase
import com.example.mygaurdian.domain.usecases.SendSOSUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.mygaurdian.data.remote.TeamRemoteDataSource
import com.example.mygaurdian.data.repository.TeamRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    companion object {
        private const val REQUEST_LOCATION = 2001
    }

    private var _b: FragmentDashboardBinding? = null
    private val b get() = _b!!

    private lateinit var fusedClient: FusedLocationProviderClient

    private val vm by viewModels<DashboardViewModel> {
        val sosDs = SOSRemoteDataSource()
        val sosRepo = SOSRepositoryImpl(sosDs)

        val teamDs = TeamRemoteDataSource(FirebaseFirestore.getInstance())
        val teamRepo = TeamRepositoryImpl(teamDs)

        DashboardViewModelFactory(
            SendSOSUseCase(sosRepo),
            GetSOSHistoryUseCase(sosRepo),
            GetTeamsUseCase(teamRepo)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentDashboardBinding.bind(view)
        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext())

        lifecycleScope.launchWhenStarted {
            vm.teams.collect { teams ->
                val names = teams.map { it.name }
                b.spinnerTeams.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    names
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            }
        }

        val adapter = SOSAdapter(emptyList())
        b.recyclerHistory.layoutManager = LinearLayoutManager(context)
        b.recyclerHistory.adapter = adapter

        lifecycleScope.launchWhenStarted {
            vm.history.collect { adapter.update(it) }
        }
        lifecycleScope.launchWhenStarted {
            vm.status.collect { msg ->
                msg?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    if (it == "FCM failed") fallbackSMS()
                    vm.clearStatus()
                }
            }
        }

        b.btnSOS.setOnClickListener { requestLocationAndSendSOS() }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationAndSendSOS() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
            return
        }
        fusedClient.lastLocation
            .addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    val selectedIndex = b.spinnerTeams.selectedItemPosition
                    val teamId = vm.teams.value.getOrNull(selectedIndex)?.id
                    if (teamId != null) {
                        vm.triggerSOS(teamId, loc.latitude, loc.longitude)
                    } else {
                        Toast.makeText(context, "No team selected", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Location unavailable", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fallbackSMS() {
        val sms = SmsManager.getDefault()
        val lat = vm.history.value.firstOrNull()?.lat
        val lng = vm.history.value.firstOrNull()?.lng
        val msg = if (lat != null && lng != null) {
            "SOS! I need help at https://maps.google.com/?q=$lat,$lng"
        } else {
            "SOS! I need help."
        }
        if (b.checkboxPolice.isChecked) {
            sms.sendTextMessage("911", null, msg, null, null)
        }
        if (b.checkboxAmbulance.isChecked) {
            sms.sendTextMessage("112", null, msg, null, null)
        }
        Toast.makeText(context, "SMS sent to authorities", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION
            && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationAndSendSOS()
        } else {
            Toast.makeText(context, "Location needed for SOS", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}

package com.example.mygaurdian.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygaurdian.R
import com.example.mygaurdian.data.remote.TeamRemoteDataSource
import com.example.mygaurdian.data.repository.TeamRepositoryImpl
import com.example.mygaurdian.databinding.FragmentHomeBinding
import com.example.mygaurdian.domain.usecases.GetTeamsUseCase
import com.example.mygaurdian.domain.usecases.ToggleLocationSharingUseCase
import com.example.mygaurdian.service.LocationService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment(R.layout.fragment_home) {

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 1001
    }

    private var _b: FragmentHomeBinding? = null
    private val b get() = _b!!

    private val viewModel by viewModels<HomeViewModel> {
        val ds = TeamRemoteDataSource(FirebaseFirestore.getInstance())
        val repo = TeamRepositoryImpl(ds)
        HomeViewModelFactory(
            GetTeamsUseCase(repo),
            ToggleLocationSharingUseCase(repo)
        )
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedClient: FusedLocationProviderClient

    private var geoCircle: Circle? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentHomeBinding.bind(view)

        b.btnCreateTeam.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createTeamFragment)
        }

        val adapter = TeamAdapter(
            teams = emptyList(),
            onShareToggled = { team, isSharing ->
                viewModel.onToggleSharing(team, isSharing)
                if (isSharing) startLocationService(team.id)
                else                stopLocationService()
            },
            onTeamClick = { team ->
                findNavController().navigate(
                    R.id.teamDetailFragment,
                    bundleOf("teamId" to team.id, "teamName" to team.name)
                )
            }
        )
        b.recyclerTeams.layoutManager = LinearLayoutManager(context)
        b.recyclerTeams.adapter = adapter

        lifecycleScope.launchWhenStarted {
            viewModel.teams.collect { list ->
                adapter.updateData(list)
                b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val mapFrag = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFrag.getMapAsync { gMap ->
            map = gMap
            enableUserLocation()
        }

        b.btnAddGeofence.setOnClickListener { addGeofence() }
        b.btnRemoveGeofence.setOnClickListener { removeGeofence() }
    }

    @SuppressLint("MissingPermission")
    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            try {
                fusedClient.lastLocation
                    .addOnSuccessListener { loc ->
                        loc?.let {
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(it.latitude, it.longitude), 15f
                                )
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                    }
            } catch (sec: SecurityException) {
                Toast.makeText(context, "Location service unavailable", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_LOCATION
            )
        }
    }

    private fun addGeofence() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_LOCATION
            )
            return
        }

        try {
            fusedClient.lastLocation
                .addOnSuccessListener { loc: Location? ->
                    if (loc == null) {
                        Toast.makeText(context, "Location unavailable", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    geoCircle?.remove()
                    geoCircle = map.addCircle(
                        CircleOptions()
                            .center(LatLng(loc.latitude, loc.longitude))
                            .radius(1000.0)
                            .strokeColor(0x5500FF00)
                            .fillColor(0x2200FF00)
                    )
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Unable to get location", Toast.LENGTH_SHORT).show()
                }
        } catch (sec: SecurityException) {
            Toast.makeText(context, "Location service unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeGeofence() {
        geoCircle?.remove()
        geoCircle = null
        map.clear()
        Toast.makeText(context, "Geofence removed", Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation()
            } else {
                Toast.makeText(
                    context,
                    "Location permission required to show your position and geofences",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startLocationService(teamId: String) {
        val intent = Intent(requireContext(), LocationService::class.java)
            .putExtra("teamId", teamId)
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    private fun stopLocationService() {
        requireContext().stopService(Intent(requireContext(), LocationService::class.java))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}

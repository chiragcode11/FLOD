package com.example.mygaurdian.presentation.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygaurdian.R
import com.example.mygaurdian.data.remote.CommunityRemoteDataSource
import com.example.mygaurdian.data.repository.CommunityRepositoryImpl
import com.example.mygaurdian.databinding.FragmentCommunityBinding
import com.example.mygaurdian.domain.model.CommunityReport
import com.example.mygaurdian.domain.usecases.AddReportUseCase
import com.example.mygaurdian.domain.usecases.GetReportsUseCase
import com.example.mygaurdian.domain.usecases.UpvoteReportUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommunityFragment : Fragment(R.layout.fragment_community) {

    companion object {
        private const val REQUEST_LOCATION = 3001
    }

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var fused: FusedLocationProviderClient

    private var heatmapOverlay: TileOverlay? = null

    private val repoImpl = CommunityRepositoryImpl(CommunityRemoteDataSource())
    private val getReportsUseCase = GetReportsUseCase(repoImpl)
    private val addReportUseCase = AddReportUseCase(repoImpl)
    private val upvoteUseCase = UpvoteReportUseCase(repoImpl)

    private val _reports = MutableStateFlow<List<CommunityReport>>(emptyList())
    private val reports: StateFlow<List<CommunityReport>> = _reports

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCommunityBinding.bind(view)
        fused = LocationServices.getFusedLocationProviderClient(requireContext())

        val mapFrag = childFragmentManager
            .findFragmentById(R.id.mapCommunity) as SupportMapFragment
        mapFrag.getMapAsync { gMap ->
            map = gMap
            requestLocationAndLoad()
        }

        val adapter = ReportListAdapter(emptyList()) { report ->
            lifecycleScope.launch {
                upvoteUseCase(report.id, /* currentUserId = */ "")
                loadReports()
            }
        }
        binding.recyclerReports.layoutManager = LinearLayoutManager(context)
        binding.recyclerReports.adapter = adapter

        lifecycleScope.launch {
            reports.collect { list ->
                adapter.update(list)
                renderHeatmap(list)
            }
        }

        binding.fabReport.setOnClickListener {
            // Navigate to report form...
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationAndLoad() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
            return
        }
        fused.lastLocation
            .addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(loc.latitude, loc.longitude), 13f
                        )
                    )
                    loadReports(loc.latitude, loc.longitude)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadReports(
        lat: Double = 0.0,
        lng: Double = 0.0,
        radius: Double = 2000.0
    ) {
        lifecycleScope.launch {
            _reports.value = getReportsUseCase(lat, lng, radius)
        }
    }

    private fun renderHeatmap(list: List<CommunityReport>) {
        if (list.isEmpty()) {
            heatmapOverlay?.remove()
            heatmapOverlay = null
            return
        }

        val weightedPoints = list.map {
            WeightedLatLng(LatLng(it.lat, it.lng), it.severity.toDouble())
        }

        val provider = HeatmapTileProvider.Builder()
            .weightedData(weightedPoints)
            .build()

        heatmapOverlay?.remove()
        heatmapOverlay = map.addTileOverlay(
            TileOverlayOptions().tileProvider(provider)
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION &&
            grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationAndLoad()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

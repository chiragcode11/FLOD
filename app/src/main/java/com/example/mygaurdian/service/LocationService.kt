package com.example.mygaurdian.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.mygaurdian.MainActivity
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LocationService : Service() {
    private lateinit var fused: FusedLocationProviderClient
    private lateinit var callback: LocationCallback
    private lateinit var teamId: String

    override fun onCreate() {
        super.onCreate()
        fused = LocationServices.getFusedLocationProviderClient(this)
        callback = object : LocationCallback() {
            override fun onLocationResult(res: LocationResult) {
                res.lastLocation?.let { sendLocation(it) }
            }
        }
        startInForeground()
        startLocationUpdates()
    }

    private fun startInForeground() {
        val chanId = "loc_chan"
        if (Build.VERSION.SDK_INT >= 26) {
            val chan = NotificationChannel(chanId, "Location", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(chan)
        }
        val notif: Notification = NotificationCompat.Builder(this, chanId)
            .setContentTitle("Sharing Location")
            .setContentIntent(
                PendingIntent.getActivity(this, 0,
                    Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
            )
            .build()
        startForeground(1, notif)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fused.requestLocationUpdates(
            LocationRequest.create().setInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
            callback,
            mainLooper
        )
    }

    private fun sendLocation(loc: Location) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val data = mapOf(
            "lat" to loc.latitude,
            "lng" to loc.longitude,
            "ts" to System.currentTimeMillis()
        )
        FirebaseFirestore.getInstance()
            .collection("teams")
            .document(teamId)
            .collection("liveLocations")
            .document(uid)
            .set(data)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        teamId = intent?.getStringExtra("teamId") ?: ""
        return START_STICKY
    }
    override fun onDestroy() {
        fused.removeLocationUpdates(callback)
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
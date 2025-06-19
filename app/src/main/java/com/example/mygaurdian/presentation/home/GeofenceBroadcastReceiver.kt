package com.example.mygaurdian.presentation.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent)
        if (event != null) {
            if (event.hasError()) {
                Log.e(TAG, "Geofence error: ${event.errorCode}")
                return
            }
        }
        if (event != null) {
            if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Log.i(TAG, "User exited geofence!")
                // TODO: send FCM notification to team or local notify
            }
        }
    }
    companion object { private const val TAG = "GeofenceReceiver" }
}

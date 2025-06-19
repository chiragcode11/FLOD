package com.example.mygaurdian

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mygaurdian.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener { pending ->
                val deep = pending?.link
                deep?.getQueryParameter("teamId")?.let { teamId ->
                    val inviterId = deep.getQueryParameter("inviterId")!!
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null) {
                        MainScope().launch {
                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(inviterId)
                                .collection("teams")
                                .document(teamId)
                                .update("memberIds", com.google.firebase.firestore.FieldValue.arrayUnion(uid))
                        }
                    }
                }
            }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.dashboardFragment,
                R.id.notificationsFragment, R.id.profileFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}

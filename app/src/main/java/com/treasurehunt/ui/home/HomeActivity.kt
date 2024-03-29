package com.treasurehunt.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.treasurehunt.R
import com.treasurehunt.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

internal const val UPLOAD_NOTIFICATION_CHANNEL_ID = "Upload"

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()

        createNotificationChannel()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_home) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = binding.bnvHome
        bottomNavigationView.setupWithNavController(navController)
        setBottomNavigationViewVisibility(navController)
    }

    private fun setBottomNavigationViewVisibility(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bnvHome.visibility = when (destination.id) {
                R.id.splashFragment -> View.GONE
                R.id.logInFragment -> View.GONE
                R.id.saveLogFragment -> View.GONE
                R.id.saveLogMapFragment -> View.GONE
                R.id.settingFragment -> View.GONE
                R.id.deleteUserFragment -> View.GONE
                else -> View.VISIBLE
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val description = getString(R.string.notification_description)
            val channel =
                NotificationChannel(UPLOAD_NOTIFICATION_CHANNEL_ID, name, importance).apply {
                    this.description = description
                }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
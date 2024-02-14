package com.treasurehunt.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.treasurehunt.R
import com.treasurehunt.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()
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
                else -> View.VISIBLE
            }
        }
    }
}
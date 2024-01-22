package com.treasurehunt.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.treasurehunt.R
import com.treasurehunt.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_home) as NavHostFragment
        val navController = navHostFragment.navController

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
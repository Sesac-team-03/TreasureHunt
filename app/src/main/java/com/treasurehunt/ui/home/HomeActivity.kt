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

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv_home) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.splashFragment -> {
                    binding.bnvHome.visibility = View.GONE
                }
                R.id.logInFragment -> {
                    binding.bnvHome.visibility = View.GONE
                }
                R.id.saveLogFragment -> {
                    binding.bnvHome.visibility = View.GONE
                }
                else -> {
                    binding.bnvHome.visibility = View.VISIBLE
                }
            }
        }
    }
}
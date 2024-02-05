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

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()
        //handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //handleDeepLink(intent)
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


    //TODO
//    val SCHEME_TREASUREHUNT = "treasurehunt"
//    val HOST_DETAIL = "detail"
//    private fun handleDeepLink(intent: Intent?) {
//        intent?.data?.let { uri ->
//            if (uri.scheme == "SCHEME_TREASUREHUNT" && uri.host == "HOST_DETAIL") {
//                val identifier = uri.lastPathSegment
//                navigateToDetailFragmentWithIdentifier(identifier)
//            }
//        }
//    }
//
//
//    private fun navigateToDetailFragmentWithIdentifier(identifier: String?) {
//        identifier?.let {
//            val bundle = Bundle().apply {
//                putString("contentId", it)
//            }
//            val navHostFragment =
//                supportFragmentManager.findFragmentById(R.id.fcv_home) as NavHostFragment
//            val navController = navHostFragment.navController
//        }
//    }
}
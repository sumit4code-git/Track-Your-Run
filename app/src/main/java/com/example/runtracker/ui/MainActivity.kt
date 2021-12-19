package com.example.runtracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.runtracker.R
import com.example.runtracker.db.RunDAO
import com.example.runtracker.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigateToTrackingFunction(intent)
        setSupportActionBar(toolbar)
        val navHostFragment= supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController= navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination,_ ->
                when(destination.id){
                    R.id.runFragment,R.id.settingsFragment,R.id.statisticFragment->
                        bottomNavigationView.visibility= View.VISIBLE
                    else-> bottomNavigationView.visibility=View.GONE
                }
            }

    }
//opens when activity is not destroyed and notification is clicked
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
         navigateToTrackingFunction(intent)
    }
//    To navigate towards tracking fragment on click of notification
    private fun navigateToTrackingFunction(intent:Intent?){
        if(intent?.action==ACTION_SHOW_TRACKING_FRAGMENT){
            navHostFragment.findNavController().navigate(R.id.action_global_tracking_fragment)
        }
    }
}
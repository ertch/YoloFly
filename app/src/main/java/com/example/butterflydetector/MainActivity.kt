package com.example.butterflydetector

import android.os.Bundle
import android.view.Menu
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.butterflydetector.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Information about Butterfly Detector", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .setAnchorView(R.id.fab).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_photoselection, R.id.nav_speciescatalog, R.id.nav_transects, R.id.nav_transectwalks
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Update drawer menu selection based on current destination
            val menuItem = when (destination.id) {
                R.id.nav_home -> navView.menu.findItem(R.id.nav_home)
                R.id.nav_photoselection -> navView.menu.findItem(R.id.nav_photoselection)
                R.id.nav_speciescatalog -> navView.menu.findItem(R.id.nav_speciescatalog)
                R.id.nav_transects -> navView.menu.findItem(R.id.nav_transects)
                R.id.nav_transectwalks -> navView.menu.findItem(R.id.nav_transectwalks)
                else -> null
            }

            // Clear all selections first
            for (i in 0 until navView.menu.size()) {
                navView.menu.getItem(i).isChecked = false
            }

            // Set the current destination as checked
            menuItem?.isChecked = true
        }

        // Custom navigation item selection listener
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.nav_home)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_photoselection -> {
                    navController.navigate(R.id.nav_photoselection)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_speciescatalog -> {
                    navController.navigate(R.id.nav_speciescatalog)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_transects -> {
                    navController.navigate(R.id.nav_transects)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_transectwalks -> {
                    navController.navigate(R.id.nav_transectwalks)
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Find bottom navigation buttons
        val photoselectionBtn = findViewById<LinearLayout>(R.id.btn_photoselection)
        val cameraBtn = findViewById<LinearLayout>(R.id.btn_camera)
        val transectsBtn = findViewById<LinearLayout>(R.id.btn_transects)

        // Set click listeners for bottom navigation
        photoselectionBtn?.setOnClickListener {
            navController.navigate(R.id.nav_photoselection)
        }

        cameraBtn?.setOnClickListener {
            Toast.makeText(this, "Automated photo capture - To be implemented later", Toast.LENGTH_SHORT).show()
        }

        transectsBtn?.setOnClickListener {
            navController.navigate(R.id.nav_transects)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

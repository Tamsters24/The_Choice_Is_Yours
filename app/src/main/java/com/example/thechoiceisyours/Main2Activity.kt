package com.example.thechoiceisyours

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.thechoiceisyours.databinding.ActivityMain2Binding

class Main2Activity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMain2Binding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        binding.apply {
            toggle = ActionBarDrawerToggle(this@Main2Activity, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_home -> {
                        navigateToDrawerItem(it.itemId)
                    }
                    R.id.nav_fast_forward -> {
                        Toast.makeText(this@Main2Activity, "Second Item Clicked", Toast.LENGTH_SHORT).show()
                    }
                    R.id.nav_rewind -> {
                        navigateToDrawerItem(it.itemId)
                    }
                }
                true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main2, menu)
        return true
    }

    /*override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }

    fun navigateToDrawerItem(itemId: Int) {
        when (itemId) {
            R.id.nav_home -> {
                val storyToMainIntent = Intent(this, MainActivity::class.java)
                startActivity(storyToMainIntent)
            }
            R.id.nav_rewind -> {
                val storyToBeginningIntent = Intent(this, StoryVolChoice::class.java)
                startActivity(storyToBeginningIntent)
            }
            // Add more cases for each drawer item here
        }
    }

}
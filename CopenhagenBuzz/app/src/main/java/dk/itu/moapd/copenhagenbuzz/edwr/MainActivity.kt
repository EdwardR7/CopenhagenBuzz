/*
 * MIT License
 *
 * Copyright (c) [2024] [Edward Rostomian]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dk.itu.moapd.copenhagenbuzz.edwr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fm = supportFragmentManager

        // Initialize the NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_timeline -> {
                    // Navigate to timelineFragment when timeline menu item is clicked
                    navController.navigate(R.id.timelineFragment)
                    true
                }

                R.id.navigation_favorites -> {
                    // Navigate to favoritesFragment when favorites menu item is clicked
                    navController.navigate(R.id.favoritesFragment)
                    true
                }

                R.id.navigation_maps -> {
                    // Navigate to mapsFragment when maps menu item is clicked
                    navController.navigate(R.id.mapsFragment)
                    true
                }

                R.id.navigation_addEvent -> {
                    // Navigate to calendarFragment when calendar menu item is clicked
                    navController.navigate(R.id.addEventFragment)
                    true
                }

                else -> false
            }
        }

        // Set up action bar with navigation controller
        setSupportActionBar(binding.toolbar)

        //handling the TopAppBar
        with(binding) {
            setSupportActionBar(toolbar)
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.login -> {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java).apply {
                            putExtra("isLoggedIn", false)
                        }
                        startActivity(intent)
                        finish()
                        true
                    }
                    R.id.logout -> {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java).apply {
                            putExtra("isLoggedIn", true)
                        }
                        startActivity(intent)
                        finish()
                        true
                    }
                    else -> false
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.login).isVisible = !intent.getBooleanExtra("isLoggedIn", false)
        menu.findItem(R.id.logout).isVisible = intent.getBooleanExtra("isLoggedIn", false)
        return true
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
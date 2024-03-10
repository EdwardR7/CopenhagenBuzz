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

/**
 * MainActivity is the entry point for the app and has navigation between fragments.
 * It has a BottomNavigationView to facilitate navigation between different fragments.
 * Also, it handles the top app bar actions, login and logout.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var dataViewModel: DataViewModel
    private var isFavoriteClicked = false

    /**
     * Called when the activity is starting.
     * @param savedInstanceState If shut down, when re-initialized, it still contains data.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up bottom navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_timeline -> {
                    navController.navigate(R.id.timelineFragment)
                    true
                }

                R.id.navigation_favorites -> {
                    navController.navigate(R.id.favoritesFragment)
                    true
                }

                R.id.navigation_maps -> {
                    navController.navigate(R.id.mapsFragment)
                    true
                }

                R.id.navigation_addEvent -> {
                    navController.navigate(R.id.addEventFragment)
                    true
                }

                else -> false
            }
        }

        // Set up action bar with navigation controller
        setSupportActionBar(binding.toolbar)

        // Set up top app bar actions
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

                    R.id.button_favorite -> {
                        // Toggle the state of the favorites button
                        isFavoriteClicked = !isFavoriteClicked
                        // Pass the current state to the TimelineFragment
                        val timelineFragment =
                            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments?.get(
                                0
                            )
                        if (timelineFragment is TimelineFragment) {
                            timelineFragment.updateFavoritesState(isFavoriteClicked)
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }

    /**
     * Initialize activity's options menu (standard options).
     * @param menu is where we place the items.
     * @return must be true for the menu to be displayed.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    /**
     * Prepare the option menu to be displayed on screen.
     * @param menu as last shown or first initialized by onCreateOptionsMenu().
     * @return must return true for the menu to be displayed.
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.login).isVisible = !intent.getBooleanExtra("isLoggedIn", false)
        menu.findItem(R.id.logout).isVisible = intent.getBooleanExtra("isLoggedIn", false)
        return true
    }

    /**
     * This method is called when the user navigates up.
     * @return true if navigation was a success and Activity was finished, false otherwise.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

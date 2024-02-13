package com.example.memorizationapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.memorizationapp.databinding.ActivityMainBinding
import com.example.memorizationapp.ui.card.CardViewModel
import com.example.memorizationapp.ui.cardList.CardListViewModel
import com.example.memorizationapp.ui.file.FileViewModel
import com.example.memorizationapp.ui.folder.FolderViewModel
import com.example.memorizationapp.ui.main.MainViewModel
import com.example.memorizationapp.ui.memorizeOption.MemorizeOptionViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel : MainViewModel by viewModels()
    private val folderViewModel : FolderViewModel by viewModels()
    private val fileViewModel : FileViewModel by viewModels()
    private val cardListViewModel : CardListViewModel by viewModels()
    private val cardViewModel : CardViewModel by viewModels()
    private val memorizeOptionViewModel : MemorizeOptionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_main
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return when (menuItem.itemId) {
            R.id.action_memorize_cards -> {
                navController.navigate(R.id.nav_memorize_option)
                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun changeFragment(id: Int){
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        when(id){
            R.id.nav_folder -> {
                navController.navigate(R.id.nav_folder)
            }
            R.id.nav_file -> {
                navController.navigate(R.id.nav_file)
            }
            R.id.nav_card_list -> {
                navController.navigate(R.id.nav_card_list)
            }
            R.id.nav_card -> {
                navController.navigate(R.id.nav_card)
            }
            R.id.nav_memorization_test -> {
                navController.navigate(R.id.nav_memorization_test)
            }
        }
    }

    fun goBack(){
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
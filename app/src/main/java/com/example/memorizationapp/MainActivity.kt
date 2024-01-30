package com.example.memorizationapp

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.memorizationapp.common.database.DBHelper
import com.example.memorizationapp.databinding.ActivityMainBinding
import com.example.memorizationapp.ui.MainActivityViewModel
import com.example.memorizationapp.ui.file.FileViewModel
import com.example.memorizationapp.ui.folder.FolderViewModel
import com.example.memorizationapp.ui.main.MainViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()
    private val folderViewModel : FolderViewModel by viewModels()
    private val fileViewModel : FileViewModel by viewModels()
    private val mainActivityViewModel : MainActivityViewModel by viewModels()

    //private val dbHelper = DBHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getFileTreeJson()

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
                R.id.nav_main, R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
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
        }
    }

    fun goBack(){
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.popBackStack()
    }

    private fun getFileTreeJson() {
        val fileTree = File(this.filesDir.absolutePath, "file_tree.json")
        if (fileTree.exists()) {
            val jsonContent = fileTree.readText()
            mainActivityViewModel.setFileTreeJson(JSONObject(jsonContent))
        } else {
            val jsonObject = JSONObject()
            val jsonArray = JSONArray()
            jsonObject.put("data", jsonArray)
            fileTree.writeText(jsonObject.toString())
            mainActivityViewModel.setFileTreeJson(jsonObject)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //dbHelper.close()
    }

}
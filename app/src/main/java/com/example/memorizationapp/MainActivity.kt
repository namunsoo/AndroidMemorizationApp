package com.example.memorizationapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.memorizationapp.databinding.ActivityMainBinding
import com.example.memorizationapp.ui.card.CardViewModel
import com.example.memorizationapp.ui.cardList.CardListViewModel
import com.example.memorizationapp.ui.file.FileViewModel
import com.example.memorizationapp.ui.folder.FolderViewModel
import com.example.memorizationapp.ui.main.MainViewModel
import com.example.memorizationapp.ui.memorizeOption.MemorizeOptionViewModel
import com.google.android.material.navigation.NavigationView


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
            R.id.nav_main -> {
                navController.navigate(R.id.nav_main)
            }
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

    // 마지막으로 뒤로 가기 버튼을 눌렀던 시간 저장
    private var backKeyPressedTime: Long = 0

    // 첫 번째 뒤로 가기 버튼을 누를 때 표시
    private var toast: Toast? = null
    override fun onBackPressed() {
        // super.onBackPressed()
        // 기존 뒤로 가기 버튼의 기능을 막기 위해 주석 처리 또는 삭제

        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지났으면 Toast 출력
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast!!.show();
            return;
        }

        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
            toast!!.cancel();
            toast = Toast.makeText(this,"이용해 주셔서 감사합니다.",Toast.LENGTH_LONG);
            toast!!.show();
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
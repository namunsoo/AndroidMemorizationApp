package com.example.memorizationapp.ui.folder

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.memorizationapp.R
import java.io.File


class CreateFolderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // 상단 바 설정
            this.supportActionBar!!.setDisplayHomeAsUpEnabled(true) // 뒤로 가기? 버튼 추가
            this.supportActionBar!!.setTitle(R.string.common_create_folder) // 제목 설정
            this.supportActionBar!!.setHomeActionContentDescription(R.string.common_back) // 길게 눌렀을 때 설명
        } catch (_: NullPointerException) {
        }

        setContentView(R.layout.activity_folder)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 체크 버튼 추가
        menuInflater.inflate(R.menu.check, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // 상단 바 클릭 이벤트
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.check -> {
                val editText = findViewById<EditText>(R.id.fileName)
                val fileName: String = editText.text.toString()

                if (fileName.isEmpty()) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setMessage(R.string.dialog_create_folder).setTitle(R.string.dialog_error_title)
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                } else {
                    saveFolder(fileName)
                    finish()
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveFolder(fileName: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        var dialog: AlertDialog
        try {
            val dir = File(filesDir?.getAbsolutePath(), fileName)
            if (dir.exists()) {
                builder.setMessage(R.string.dialog_file_exist)
                    .setTitle(R.string.dialog_error_title)
                dialog = builder.create()
                dialog.show()
            } else {
                dir.mkdirs()
            }

        } catch(e: Exception) {
            builder.setMessage(R.string.dialog_file_save_error)
                .setTitle(R.string.dialog_error_title)
            dialog = builder.create()
            dialog.show()
        }
    }
}
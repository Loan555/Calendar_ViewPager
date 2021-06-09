package com.loan555.myviewpager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.loan555.myviewpager.model.AppPreferences
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        AppPreferences.init(this)
        val oldPass = AppPreferences.password
        if (oldPass == "") {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            text_name.text = "Xin chào ${AppPreferences.userName}"
        }

        bt_login.setOnClickListener {
            AppPreferences.init(this)
            val pass = editTextTextPasswordLogin.text.toString()
            if (pass == oldPass) {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

}
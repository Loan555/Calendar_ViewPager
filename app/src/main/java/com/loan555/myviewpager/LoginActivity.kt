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

        checkPass()
        eventBody()
    }

    private fun checkPass() {
        AppPreferences.init(this)
        if (AppPreferences.password == "") {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            setContentView(R.layout.activity_login)
            text_name.text = "Xin chào ${AppPreferences.userName}"
        }

    }

    private fun eventBody() {
        bt_login.setOnClickListener {
            if (login()) {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun login(): Boolean {
        if (editTextTextPasswordLogin.text.toString() == AppPreferences.password) {
            return true
        } else
            Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
        return false
    }
}
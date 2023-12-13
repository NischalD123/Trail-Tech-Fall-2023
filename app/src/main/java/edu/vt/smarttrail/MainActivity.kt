package edu.vt.smarttrail

import android.content.Context
import android.content.Intent
import android.os.Bundle

import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import edu.vt.smarttrail.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_home_activity)

        val create_button = findViewById<Button>(R.id.create_an_account_btn) // Find the button by its ID
        create_button.setOnClickListener {
            val intent = Intent(this, MainHomeLogin::class.java)
            startActivity(intent)
        }
        val sign_button = findViewById<Button>(R.id.sign_in_button) // Find the button by its ID
        sign_button.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }


        val login_db = DBHelper(this)

        val sharedPre = getSharedPreferences("autoLogin", Context.MODE_PRIVATE)
        val savedKey = sharedPre.getString("key", null)
        if (savedKey != null) {
            val user = login_db.getUser(savedKey)

            val intent = Intent(this, HomeActivity::class.java)
            UidSingleton.setUid(user.username)
            intent.putExtra("primary_key", user.primaryKey)
            intent.putExtra("userID", user.username)
            intent.putExtra("password", user.password)
            intent.putExtra("email", user.email)
            startActivity(intent)
        }


    }
}

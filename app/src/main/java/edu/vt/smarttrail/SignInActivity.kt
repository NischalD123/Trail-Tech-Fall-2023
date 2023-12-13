package edu.vt.smarttrail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val actionBar = supportActionBar

        // Creates back button.
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        var username: TextInputEditText = findViewById<View>(R.id.username) as TextInputEditText
        var password: TextInputEditText = findViewById<View>(R.id.password) as TextInputEditText
        var tvForgotPassword: TextView = findViewById<View>(R.id.tvForgotPassword) as TextView
        var sign_in: Button = findViewById<View>(R.id.sign_in) as Button

        var login_db:DBHelper = DBHelper(this);

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        username.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        password.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        sign_in.setOnClickListener {
            var user = username.text.toString()
            var pass = password.text.toString()
            UidSingleton.setUid(user)
            if(user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fill all the fields.", Toast.LENGTH_SHORT).show();
            }
            else {
                val valid = login_db.check_username_password(user, pass)
                if (valid) {
                    val primary_key = login_db.get_user_key(user, pass)
                    val email = login_db.getEmail(user, pass)
                    Log.e("SIN", "sdlfjk ${user} : ${pass}")
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("primary_key", primary_key)
                    intent.putExtra("userID", user)
                    intent.putExtra("password", pass)
                    intent.putExtra("email", email)

                    val sharedPrefer = getSharedPreferences("autoLogin", Context.MODE_PRIVATE)
                    sharedPrefer.edit().putString("key", primary_key).apply()
                    val savedKey = sharedPrefer.getString("key", null)
                    Log.e("SIN", savedKey!!)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(this, "Invalid Username or Password.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Hides keyboard.
    private fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
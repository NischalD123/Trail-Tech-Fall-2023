package edu.vt.smarttrail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.view.View.OnFocusChangeListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        // Creates an action bar, where back button is.
        val actionBar = supportActionBar

        // Creates back button.
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        val firebaseCloud = FirebaseDatabase.getInstance().getReference("users")

        val newPasswordText = findViewById<View>(R.id.new_password) as TextInputEditText
        val etEmail = findViewById<View>(R.id.etEmail) as TextInputEditText
        newPasswordText.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        val submit = findViewById<View>(R.id.submit_password) as Button
        submit.setOnClickListener {
            val newPassword = newPasswordText.text.toString()
            val email = etEmail.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Fill the fields.", Toast.LENGTH_SHORT).show()
            }else if (newPassword.isEmpty()) {
                Toast.makeText(this, "Fill the fields.", Toast.LENGTH_SHORT).show()
            }
            else {

                val dbHelper = DBHelper(this)
                val userExists = dbHelper.check_existing_email(email)

                if (userExists) {
                    // Get the user's key
                    val userKey = dbHelper.getUserKeyByEmail(email)

                    // Update the password in the database
                    dbHelper.changePassword(userKey, newPassword)
                    firebaseCloud.child(userKey).child("password").setValue(newPassword)

                    Toast.makeText(
                        this,
                        "Password reset successful",
                        Toast.LENGTH_LONG
                    ).show()

                    finish() // Close the ForgotPasswordActivity
                } else {
                    Toast.makeText(this, "Email not found.", Toast.LENGTH_SHORT).show()
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
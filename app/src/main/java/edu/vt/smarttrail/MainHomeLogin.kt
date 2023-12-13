package edu.vt.smarttrail

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase


class MainHomeLogin : AppCompatActivity() {
    //, LifecycleOwner {

    private val firebaseCloud = FirebaseDatabase.getInstance().getReference("users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val username: TextInputEditText = findViewById<View>(R.id.username) as TextInputEditText
        val password: TextInputEditText = findViewById<View>(R.id.password) as TextInputEditText
        val repassword: TextInputEditText = findViewById<View>(R.id.repassword) as TextInputEditText
        val email: TextInputEditText = findViewById<View>(R.id.email) as TextInputEditText

        // Add ability to click off input fields.
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

        repassword.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        email.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        val sign_up = findViewById<View>(R.id.sign_up) as Button

        val login_db = DBHelper(this)

        sign_up.setOnClickListener {
            val user = username.text.toString()
            val pass = password.text.toString()
            val repass = repassword.text.toString()
            val email = email.text.toString().toLowerCase()

            if (user.isEmpty() || pass.isEmpty() || repass.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Fill all the fields.", Toast.LENGTH_SHORT).show()
            } else {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this@MainHomeLogin, "Invalid Email.", Toast.LENGTH_LONG).show()
                } else if (pass.equals(repass)) {
                    val existing = login_db.check_username(user)
                    val existEmail = login_db.check_existing_email(email)

                    if (!existing && !existEmail) {
                        // Upload to Cloud
                        val reference = firebaseCloud.push()
                        val key:String = reference.key.toString()
                        val userCloud: User = User(key, user, email, pass, "No Data Available")
                        reference.setValue(userCloud)

                        // Upload to Sqlite
                        val inserted = login_db.insertUserData(key, user, pass, email)

                        if (inserted) {
                            Toast.makeText(this, "Registration Successful.", Toast.LENGTH_SHORT)
                                .show();

                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)

                        } else {
                            Toast.makeText(this, "Registration Failed.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (existEmail) {
                            Toast.makeText(this, "Email Already in use.", Toast.LENGTH_SHORT)
                                .show();
                        } else {
                            Toast.makeText(this, "User Exists Already.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords not matching.", Toast.LENGTH_SHORT).show()
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

//        var username: EditText = findViewById<View>(R.id.username) as EditText;
//        var password: EditText = findViewById<View>(R.id.password) as EditText;
//        var repassword: EditText = findViewById<View>(R.id.repassword) as EditText;
//        var email: EditText = findViewById<View>(R.id.email) as EditText;
//
//        var sign_up = findViewById<View>(R.id.sign_up) as Button;
//        var sign_in = findViewById<View>(R.id.sign_in) as Button;
//
//        var login_db: DBHelper = DBHelper(this);
//
//        sign_up.setOnClickListener {
//            var user = username.text.toString();
//            var pass = password.text.toString();
//            var repass = repassword.text.toString();
//            var email = email.text.toString();
//
//            if (user.isEmpty() || pass.isEmpty() || repass.isEmpty() || email.isEmpty()) {
//                Toast.makeText(this, "Fill all the fields.", Toast.LENGTH_SHORT).show();
//            }
//            else {
//                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                    Toast.makeText(this@MainActivity, "Invalid Email.", Toast.LENGTH_LONG).show()
//                }
//                else if (pass.equals(repass)) {
//                    val existing = login_db.check_username(user);
//                    val existEmail = login_db.check_existing_email(email);
//
//                    if (!existing && !existEmail) {
//                        val inserted = login_db.insertUserData(user, pass, email);
//
//                        if (inserted) {
//                            Toast.makeText(this, "Registration Successful.", Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            Toast.makeText(this, "Registration Failed.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    else {
//                        if (existEmail) {
//                            Toast.makeText(this, "Email Already in use.", Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            Toast.makeText(this, "User Exists Already.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//                else {
//                    Toast.makeText(this, "Passwords not matching.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//
//        // Sends to sign in activity when pressed.
//        sign_in.setOnClickListener {
//            val intent = Intent(this, SignInActivity::class.java)
//            startActivity(intent)
//        }
//    override fun getLifecycle(): Lifecycle {
//        return lifecycleRegistry
//    }
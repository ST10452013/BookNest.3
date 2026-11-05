
package com.example.booknest


import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.content.res.Configuration
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val languageSpinner = findViewById<Spinner>(R.id.languageSpinner)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.language_options,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        languageSpinner.adapter = adapter

        languageSpinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    val languageCode = when (position) {
                        0 -> "en"
                        1 -> "zu"
                        2 -> "st"
                        else -> "en"
                    }

                    val preferences = getSharedPreferences(
                        "BookNestSettings",
                        MODE_PRIVATE
                    )

                    val savedLanguage = preferences.getString("language", "en")

                    if (savedLanguage != languageCode) {
                        preferences.edit()
                            .putString("language", languageCode)
                            .apply()

                        val locale = Locale(languageCode)
                        Locale.setDefault(locale)

                        val config = Configuration(resources.configuration)
                        config.setLocale(locale)

                        recreate()
                    }
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {}
            }

        auth = FirebaseAuth.getInstance()

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (email.isBlank()) {
                emailInput.error = "Please enter your email"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Enter a valid email address"
                return@setOnClickListener
            }

            if (password.isBlank()) {
                passwordInput.error = "Please enter your password"
                return@setOnClickListener
            }

            loginButton.isEnabled = false

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    loginButton.isEnabled = true

                    if (task.isSuccessful) {
                        val intent = Intent(
                            this@MainActivity,
                            HomeActivity::class.java
                        )
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            task.exception?.localizedMessage
                                ?: "Login failed. Check your details.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        registerButton.setOnClickListener {
            val intent = Intent(
                this@MainActivity,
                RegisterActivity::class.java
            )
            startActivity(intent)
        }
    }
}

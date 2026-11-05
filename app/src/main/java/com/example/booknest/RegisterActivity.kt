package com.example.booknest

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val emailInput = findViewById<EditText>(R.id.registerEmailInput)
        val passwordInput = findViewById<EditText>(R.id.registerPasswordInput)
        val confirmPasswordInput =
            findViewById<EditText>(R.id.confirmPasswordInput)

        val createAccountButton =
            findViewById<Button>(R.id.createAccountButton)
        val backButton = findViewById<Button>(R.id.backToLoginButton)

        createAccountButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            when {
                email.isBlank() -> {
                    emailInput.error = "Please enter your email"
                    emailInput.requestFocus()
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    emailInput.error = "Enter a valid email address"
                    emailInput.requestFocus()
                }

                password.length < 6 -> {
                    passwordInput.error =
                        "Password must contain at least 6 characters"
                    passwordInput.requestFocus()
                }

                password != confirmPassword -> {
                    confirmPasswordInput.error = "Passwords do not match"
                    confirmPasswordInput.requestFocus()
                }

                else -> {
                    createAccountButton.isEnabled = false

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            createAccountButton.isEnabled = true

                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Account created successfully!",
                                    Toast.LENGTH_LONG
                                ).show()

                                // Return to the login screen.
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    task.exception?.localizedMessage
                                        ?: "Registration failed. Please try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}
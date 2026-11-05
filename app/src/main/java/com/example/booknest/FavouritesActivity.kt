
package com.example.booknest

import android.os.Bundle
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavouritesActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var favouritesList: LinearLayout
    private lateinit var message: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        favouritesList = findViewById(R.id.favouritesList)
        message = findViewById(R.id.favouritesMessage)

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadFavourites()
    }

    private fun loadFavourites() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            message.text = "Please log in to view favourites."
            return
        }

        message.text = "Loading favourites..."
        favouritesList.removeAllViews()

        db.collection("users")
            .document(uid)
            .collection("favourites")
            .get()
            .addOnSuccessListener { documents ->

                favouritesList.removeAllViews()

                if (documents.isEmpty) {
                    message.text = "You have not saved any favourites yet."
                    return@addOnSuccessListener
                }

                message.text = "Your saved books"

                for (document in documents) {
                    val title = document.getString("title")
                        ?: "Unknown title"
                    val author = document.getString("author")
                        ?: "Unknown author"
                    val year = document.getString("year") ?: ""

                    val card = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(20, 20, 20, 20)
                        setBackgroundColor(Color.WHITE)
                    }

                    val details = TextView(this).apply {
                        text = "$title\nAuthor: $author\n$year"
                        textSize = 16f
                        setTextColor(Color.DKGRAY)
                    }

                    val removeButton = Button(this).apply {
                        text = "Remove Favourite"
                        setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(
                                Color.rgb(119, 119, 119)
                            )
                        )
                        setTextColor(Color.WHITE)
                    }

                    removeButton.setOnClickListener {
                        removeButton.isEnabled = false

                        document.reference.delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@FavouritesActivity,
                                    "Favourite removed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loadFavourites()
                            }
                            .addOnFailureListener {
                                removeButton.isEnabled = true
                                Toast.makeText(
                                    this@FavouritesActivity,
                                    "Could not remove favourite.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                    card.addView(details)
                    card.addView(removeButton)

                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 16
                    }

                    favouritesList.addView(card, params)
                }
            }
            .addOnFailureListener {
                message.text = "Could not load favourites. Please try again."
            }
    }
}

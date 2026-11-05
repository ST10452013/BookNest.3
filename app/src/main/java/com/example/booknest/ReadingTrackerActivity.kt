
package com.example.booknest

import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import kotlin.jvm.java

class ReadingTrackerActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var trackerList: LinearLayout
    private lateinit var message: TextView

    private val statuses = arrayOf(
        "Want to Read",
        "Currently Reading",
        "Completed"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_tracker)

        trackerList = findViewById(R.id.trackerList)
        message = findViewById(R.id.trackerMessage)

        findViewById<Button>(R.id.viewReadingRecordButton)
            .setOnClickListener {
                startActivity(
                    Intent(this@ReadingTrackerActivity, ReadingRecordActivity::class.java)
                )
            }

        findViewById<Button>(R.id.trackerBackButton)
            .setOnClickListener {
                finish()
            }
    }



    override fun onResume() {
        super.onResume()
        loadReadingList()
    }

    private fun loadReadingList() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            message.text = "Please log in to view your reading tracker."
            return
        }

        message.text = "Loading your reading list..."
        trackerList.removeAllViews()

        db.collection("users")
            .document(uid)
            .collection("readingList")
            .get()
            .addOnSuccessListener { documents ->

                trackerList.removeAllViews()

                if (documents.isEmpty) {
                    message.text =
                        "Your reading list is empty. Add books from search results."
                    return@addOnSuccessListener
                }

                message.text = "Your reading progress"

                for (document in documents) {
                    val title = document.getString("title")
                        ?: "Unknown title"
                    val author = document.getString("author")
                        ?: "Unknown author"
                    val status = document.getString("status")
                        ?: "Want to Read"

                    val card = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        gravity = Gravity.CENTER_VERTICAL
                        setPadding(20, 20, 20, 20)
                        setBackgroundColor(Color.WHITE)
                    }

                    val details = TextView(this).apply {
                        text = "$title\nAuthor: $author\nStatus: $status"
                        textSize = 16f
                        setTextColor(Color.DKGRAY)
                    }

                    val updateButton = Button(this).apply {
                        text = "Update Reading Status"
                        setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(
                                Color.rgb(36, 77, 59)
                            )
                        )
                        setTextColor(Color.WHITE)
                    }

                    updateButton.setOnClickListener {
                        AlertDialog.Builder(this)
                            .setTitle("Choose reading status")
                            .setItems(statuses) { _, which ->
                                updateStatus(
                                    document.id,
                                    statuses[which]
                                )
                            }
                            .show()
                    }

                    val removeButton = Button(this).apply {
                        text = "Remove from Tracker"
                    }

                    removeButton.setOnClickListener {
                        document.reference.delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@ReadingTrackerActivity,
                                    "Book removed from tracker.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loadReadingList()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@ReadingTrackerActivity,
                                    "Could not remove book.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                    card.addView(details)
                    card.addView(updateButton)
                    card.addView(removeButton)

                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 16
                    }

                    trackerList.addView(card, params)
                }
            }
            .addOnFailureListener {
                message.text = "Could not load your reading list."
            }
    }

    private fun updateStatus(bookId: String, newStatus: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("readingList")
            .document(bookId)
            .update(
                mapOf(
                    "status" to newStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Reading status updated!",
                    Toast.LENGTH_SHORT
                ).show()
                loadReadingList()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Could not update reading status.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}


package com.example.booknest

import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReadingRecordActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recordList: LinearLayout
    private lateinit var message: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_record)

        recordList = findViewById(R.id.recordList)
        message = findViewById(R.id.recordMessage)

        findViewById<Button>(R.id.recordBackButton)
            .setOnClickListener {
                finish()
            }
    }

    override fun onResume() {
        super.onResume()
        loadReadingRecord()
    }

    private fun loadReadingRecord() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            message.text = "Please log in to view your reading record."
            return
        }

        message.text = "Loading your completed books..."
        recordList.removeAllViews()

        db.collection("users")
            .document(uid)
            .collection("readingList")
            .whereEqualTo("status", "Completed")
            .get()
            .addOnSuccessListener { documents ->

                recordList.removeAllViews()

                if (documents.isEmpty) {
                    message.text =
                        "Your reading record is waiting for its first story!"

                    val emptyCard = TextView(this).apply {
                        text = "📚\n\nYour bookshelf of completed adventures is empty.\n\n" +
                                "Visit your Reading Tracker and mark a book as Completed " +
                                "to see it appear here."
                        textSize = 17f
                        gravity = Gravity.CENTER
                        setTextColor(Color.rgb(36, 77, 59))
                        setPadding(28, 40, 28, 40)
                        background = GradientDrawable().apply {
                            setColor(Color.WHITE)
                            cornerRadius = 24f
                            setStroke(2, Color.rgb(196, 154, 90))
                        }
                    }

                    recordList.addView(
                        emptyCard,
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    )
                    return@addOnSuccessListener
                }

                message.text =
                    "Wonderful! You have completed ${documents.size()} book(s)."

                for (document in documents) {
                    val title = document.getString("title")
                        ?: "Unknown title"

                    val author = document.getString("author")
                        ?: "Unknown author"

                    val year = document.getString("year")
                        ?: "Publication year unavailable"

                    val card = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(20, 20, 20, 20)
                        background = GradientDrawable().apply {
                            setColor(Color.WHITE)
                            cornerRadius = 20f
                            setStroke(1, Color.LTGRAY)
                        }
                    }

                    val titleView = TextView(this).apply {
                        text = "📖  $title"
                        textSize = 19f
                        setTextColor(Color.rgb(36, 77, 59))
                        setTypeface(null, Typeface.BOLD)
                    }

                    val detailsView = TextView(this).apply {
                        text = "Author: $author\nYear: $year\nStatus: Completed ✓"
                        textSize = 15f
                        setTextColor(Color.DKGRAY)
                        setPadding(0, 12, 0, 0)
                    }

                    card.addView(titleView)
                    card.addView(detailsView)

                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 16
                    }

                    recordList.addView(card, params)
                }
            }
            .addOnFailureListener {
                message.text =
                    "Could not load your reading record. Please try again."
            }
    }
}

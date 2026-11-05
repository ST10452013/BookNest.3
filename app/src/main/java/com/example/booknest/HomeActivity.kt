package com.example.booknest

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.app.AlertDialog

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val db = FirebaseFirestore.getInstance()
    private lateinit var searchInput: EditText
    private lateinit var searchButton: Button
    private lateinit var message: TextView
    private lateinit var resultsContainer: LinearLayout

    private val executor = Executors.newFixedThreadPool(4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        searchInput = findViewById(R.id.bookSearchInput)
        searchButton = findViewById(R.id.searchBooksButton)
        message = findViewById(R.id.homeMessage)
        resultsContainer = findViewById(R.id.bookResults)

        findViewById<Button>(R.id.logoutButton)
            .setOnClickListener {
                auth.signOut()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish()
            }

        // Open the My Favourites screen
        findViewById<Button>(R.id.favouritesButton)
            .setOnClickListener {
                val intent = Intent(this, FavouritesActivity::class.java)
                startActivity(intent)
            }

// Open the Reading Tracker screen
        findViewById<Button>(R.id.readingTrackerButton)
            .setOnClickListener {
                val intent = Intent(this, ReadingTrackerActivity::class.java)
                startActivity(intent)
            }

        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()

            if (query.isBlank()) {
                searchInput.error = "Enter a book title or author"
                return@setOnClickListener
            }

            searchBooks(query)
        }
    }

    private fun searchBooks(query: String) {
        searchButton.isEnabled = false
        message.text = "Searching for books..."
        resultsContainer.removeAllViews()

        executor.execute {
            try {
                val encoded = URLEncoder.encode(query, "UTF-8")
                val address =
                    "https://openlibrary.org/search.json" +
                            "?q=$encoded" +
                            "&fields=key,title,author_name," +
                            "first_publish_year,cover_i&limit=10"

                val connection =
                    URL(address).openConnection() as HttpURLConnection

                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.setRequestProperty(
                    "User-Agent",
                    "BookNestAndroid/1.0"
                )

                val jsonText = try {
                    if (connection.responseCode != 200) {
                        throw Exception("Book search request failed")
                    }

                    connection.inputStream.bufferedReader()
                        .use { it.readText() }
                } finally {
                    connection.disconnect()
                }

                val json = JSONObject(jsonText)
                val books = json.getJSONArray("docs")

                runOnUiThread {
                    searchButton.isEnabled = true
                    resultsContainer.removeAllViews()

                    if (books.length() == 0) {
                        message.text =
                            "No books found. Try another search."
                        return@runOnUiThread
                    }

                    message.text =
                        "Found ${books.length()} results for \"$query\""

                    for (i in 0 until books.length()) {
                        addBookCard(books.getJSONObject(i))
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    searchButton.isEnabled = true
                    message.text =
                        "Could not load books. Check your internet connection."
                }
            }
        }
    }

    private fun addBookCard(book: JSONObject) {
        val title = book.optString("title", "Unknown title")
        val authors = book.optJSONArray("author_name")

        val author = if (authors != null && authors.length() > 0) {
            authors.optString(0, "Unknown author")
        } else {
            "Unknown author"
        }

        val year = if (
            book.has("first_publish_year") &&
            !book.isNull("first_publish_year")
        ) {
            book.optInt("first_publish_year").toString()
        } else {
            "Publication year unavailable"
        }

        val workKey = book.optString("key", "")
        val coverId = book.optInt("cover_i", -1)

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(12, 12, 12, 12)
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = 16f
                setStroke(1, Color.LTGRAY)
            }
            isClickable = true
            isFocusable = true
            contentDescription = "$title by $author. Open book details."
        }

        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 12
        }

        val cover = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(100, 145)
            scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundColor(Color.LTGRAY)
            contentDescription = "Cover of $title"
        }

        val details = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(14, 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val titleView = TextView(this).apply {
            text = title
            textSize = 17f
            setTextColor(Color.rgb(36, 77, 59))
            setTypeface(null, Typeface.BOLD)
        }

        val authorView = TextView(this).apply {
            text = "Author: $author"
            textSize = 14f
            setTextColor(Color.DKGRAY)
            setPadding(0, 8, 0, 0)
        }

        val yearView = TextView(this).apply {
            text = year
            textSize = 13f
            setTextColor(Color.GRAY)
            setPadding(0, 6, 0, 0)
        }

        val openHint = TextView(this).apply {
            text = "Tap to view book details →"
            textSize = 12f
            setTextColor(Color.rgb(36, 77, 59))
            setPadding(0, 12, 0, 0)
        }

        details.addView(titleView)
        details.addView(authorView)
        details.addView(yearView)
        details.addView(openHint)

        val favouriteButton = Button(this).apply {
            text = "Add to Favourites"
            setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                    Color.rgb(36, 77, 59)
                )
            )
            setTextColor(Color.WHITE)
        }

        details.addView(favouriteButton)

        favouriteButton.setOnClickListener {
            saveFavourite(
                workKey,
                title,
                author,
                year,
                if (coverId > 0) {
                    "https://covers.openlibrary.org/b/id/$coverId-M.jpg"
                } else {
                    ""
                },
                favouriteButton
            )
        }

        card.addView(cover)
        card.addView(details)
        resultsContainer.addView(card, cardParams)

        card.setOnClickListener {
            if (workKey.startsWith("/works/")) {
                val url = "https://openlibrary.org$workKey"
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                )
            } else {
                Toast.makeText(
                    this,
                    "Book details are not available for this result.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (coverId > 0) {
            val imageUrl =
                "https://covers.openlibrary.org/b/id/$coverId-M.jpg"

            executor.execute {
                try {
                    val connection =
                        URL(imageUrl).openConnection() as HttpURLConnection

                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000

                    val bitmap = try {
                        connection.inputStream.use {
                            BitmapFactory.decodeStream(it)
                        }
                    } finally {
                        connection.disconnect()
                    }

                    if (bitmap != null) {
                        runOnUiThread {
                            if (!isFinishing && !isDestroyed) {
                                cover.setImageBitmap(bitmap)
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Keep the grey cover placeholder if loading fails.
                }
            }
        }
    }

    private fun saveFavourite(
        workKey: String,
        title: String,
        author: String,
        year: String,
        coverUrl: String,
        button: Button
    ) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(
                this,
                "Please log in to save favourites.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!workKey.startsWith("/works/")) {
            Toast.makeText(
                this,
                "This book cannot be saved.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val bookId = workKey.substringAfterLast("/")

        if (bookId.isBlank()) {
            Toast.makeText(
                this,
                "Invalid book information.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        button.isEnabled = false
        button.text = "Saving..."

        val favourite = hashMapOf(
            "workKey" to workKey,
            "title" to title,
            "author" to author,
            "year" to year,
            "coverUrl" to coverUrl,
            "savedAt" to FieldValue.serverTimestamp()
        )

        db.collection("users")
            .document(userId)
            .collection("favourites")
            .document(bookId)
            .set(favourite)
            .addOnSuccessListener {
                button.isEnabled = true
                button.text = "Saved to Favourites"

                Toast.makeText(
                    this,
                    "$title added to favourites!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { exception ->
                button.isEnabled = true
                button.text = "Add to Favourites"

                Toast.makeText(
                    this,
                    "Could not save favourite: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
Youtube link:https://youtu.be/e_5exuCUB80?si=DXct6yBOq0OpWA88

https://youtu.be/JPR4srWfY_A?si=D8GX_JTU7n8HLHsn

📚 BookNest – Android Library Application

Project Overview BookNest is an Android-based library and book discovery application developed using Kotlin and XML layouts in Android Studio.
The application provides users with a platform to discover books, search for book information, save favourite books and manage their personal reading progress. It integrates Firebase services for user authentication and cloud data storage, together with the Open Library API to retrieve book information.

The main aim of BookNest is to encourage reading by providing users with a simple and organised way to discover books and keep track of their reading journey.

Features and Functionality 2.1 User Registration and Login Users can create an account using an email address and password. Existing users can log in using their registered credentials. Firebase Authentication manages account registration and login. Basic input validation helps prevent incomplete or invalid entries. 2.2 Book Search and Discovery Users can search for books using the Open Library API. Search results display available book information, including: Book title. Author. Publication year. Book cover image. Users can select a book to open its Open Library page for further information and available reading or borrowing options. 2.3 My Favourites Users can save books they are interested in reading. Favourite books are stored in Firebase Firestore. Users can view their saved books. Users can remove books from their favourites list. 2.4 Reading Tracker Users can add books to their personal reading list. Each book can have one of three reading statuses: Want to Read. Currently Reading. Completed. Users can update the reading status of a book. Users can remove books from their reading tracker. 2.5 Reading Record Displays books marked as Completed. Retrieves completed books from the user's Firestore reading list. Displays the total number of completed books. Provides an empty-state message when no completed books are available. 2.6 User Logout Users can securely end their authenticated session through Firebase Authentication. After logging out, users are returned to the login screen.
Technologies and Tools Used Technology Purpose Kotlin Main application programming language XML Android user interface layouts Android Studio Application development environment Firebase Authentication User registration and login Firebase Firestore Cloud database for favourites and reading lists Open Library API Book search and information retrieval Gradle Project configuration and dependency management Git and GitHub Source code management and version control GitHub Actions Automated application build workflow
Application Structure The application is organised into separate Activities, each responsible for a specific function.
Activity Responsibility MainActivity User login RegisterActivity User registration HomeActivity Book search and navigation FavouritesActivity Display and manage favourite books ReadingTrackerActivity Manage reading progress ReadingRecordActivity Display completed books The application uses separate XML layout files to define the user interface for each Activity.

External API Integration BookNest integrates the Open Library Search API to retrieve book information.
API endpoint:

https://openlibrary.org/search.json

The application sends search requests to the API and processes the returned JSON response to retrieve available book information.

Book cover images are retrieved using the Open Library Covers API:

https://covers.openlibrary.org/b/id/

Open Library is an external service and its availability is required for online book searches.

Website: https://openlibrary.org/

API documentation: https://openlibrary.org/developers/api

Firebase Integration BookNest uses Firebase for authentication and cloud data storage.
Firebase Authentication Firebase Authentication manages user registration and login using email and password credentials.

Cloud Firestore Firestore stores user-specific information, including favourite books and reading tracker records.

The database structure follows this general organisation:

text users └── userId ├── favourites │ └── bookId │ └── readingList └── bookId

Each user's information is stored under their unique Firebase Authentication user ID.

Firestore security rules are configured to restrict access so that authenticated users can only read and write their own user documents.

Installation and Setup Requirements Android Studio. Android SDK. Kotlin support. A Firebase project. Internet connection for Firebase and Open Library services. An Android device or emulator. Setup Instructions Clone or download the BookNest repository from GitHub. Open the project in Android Studio. Allow Gradle to synchronise the project dependencies. Configure Firebase for the Android application. Place the appropriate google-services.json file inside the app module. Confirm that the Firebase Authentication and Cloud Firestore services are enabled. Connect an Android device or start an emulator. Build and run the application. Note: Firebase configuration must match the application's package name: com.example.booknest.
Do not include private service-account credentials or server keys in the repository.

Running the Application Once the project has been configured:
Open the project in Android Studio. Select the Android device or emulator. Click the Run button. Register a new account or log in using an existing account. Start discovering books and using the available reading management features. 9. Testing The application should be tested to verify that its main functionality works correctly.

Suggested test scenarios include:

Test Expected result Register with valid details Account is created successfully Log in with invalid credentials An appropriate error message is displayed Search for an existing book Relevant book results are displayed Save a favourite book Book appears in My Favourites Update a reading status New status is saved and displayed Additional testing should include invalid and empty inputs, unsuccessful API requests, and Firestore operations.

GitHub and Version Control GitHub is used to store and manage the BookNest source code.
The repository supports version control by allowing development changes to be committed and tracked over time.

GitHub Actions can also be used to automate Android build checks whenever code is pushed to the repository or a pull request is created.

Future Improvements Possible improvements for future versions include:
Expanded multilingual support. Book notes and personal reading reflections. Reading statistics and progress summaries. Improved book recommendations. Additional user interface enhancements. Automated unit and UI testing. Preparation for Google Play Store distribution. 12. AI Usage Declaration Artificial Intelligence tools, including ChatGPT, were used as development support during the creation of BookNest. AI assistance was used to help understand Kotlin and XML concepts, troubleshoot programming errors, explain Firebase Authentication and Firestore integration, and guide the implementation and refinement of application features. This assistance included guidance relating to user login and registration, book searching through the Open Library API, saving and removing favourite books, managing reading statuses, and displaying completed books in the Reading Record. AI was also used to assist with project documentation and explanations of the application's code structure. The application was developed, assembled and tested by the student, who remains responsible for the final implementation, verification and submitted work. AI was used as a support tool rather than as a feature within the BookNest application itself.

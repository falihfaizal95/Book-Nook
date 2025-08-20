
BookNook App 📚






Overview

BookNook is a JavaFX desktop application designed to provide users with an engaging way to explore language and literature. What started off as an idea that I had, this version of the app has been significantly expanded and independently developed to showcase seamless API integration, responsive UI design, and performance optimization.

Features

🌐 Integrates two RESTful JSON APIs:
Free Dictionary API: Provides word definitions and phonetic information.
Google Books API: Returns book titles and metadata related to a given keyword or phrase.
🔄 Dynamically links output from the dictionary API to query relevant books using the second API.
🎨 Intuitive, responsive GUI built with JavaFX 17 for a clean and interactive user experience.
💡 Caching mechanism that reduces redundant API calls by up to 40%, improving speed and responsiveness.
🔧 Maven-based project structure for streamlined dependency management and builds.
🧭 Smooth navigation system to display search results clearly and effectively.
Technologies Used

Java 17
JavaFX 17
Gson (for JSON parsing)
java.net.http.HttpClient (for HTTP requests)
Maven (for dependency management)
VS Code & Emacs (development IDEs)
Git (version control)
How It Works

The user enters a word or phrase in the search bar.
The app queries the Free Dictionary API to retrieve definitions and phonetics.
The word is then passed to the Google Books API to fetch relevant book titles and information.
Results are displayed through a visually intuitive interface, with cached results loading instantly when revisited.
Getting Started

To run BookNook locally:

git clone https://github.com/your-username/booknook-app.git
cd booknook-app
# Open the project in IntelliJ or VS Code
# Ensure Java 17 and JavaFX are installed
# Build using Maven and run the Main class
Requirements

Java 17+
Maven
Internet connection (to access external APIs)
Screenshots



Future Improvements

Add offline access mode using local file-based cache
Expand to support audio pronunciation playback
Add favorites/bookmarking system for saved results
Implement dark mode and theme settings
License



Acknowledgments


API Providers:
Free Dictionary API
Google Books API
>>>>>>> dd33d41f29427fd0fdb4c3296c4fa6d6bd00e474

# 🎬 Movie Trailer App

A beautiful Jetpack Compose app that displays popular movies using [The Movie Database (TMDb)](https://www.themoviedb.org/) API. Users can watch trailers, search movies, and mark favorites — all with a Material 3 UI.

---

## ✨ Features

- 🔥 Browse trending/popular movies
- 🔍 **Search** by movie title
- 🎥 Watch trailers via embedded **YouTube** or **ExoPlayer**
- ❤️ Mark/unmark movies as **favorites**
- 💾 Persistent favorites using SharedPreferences (DataStore optional)
- 🎨 Fully styled with **Material 3** and dark theme
- 📦 Built using **Jetpack Compose**, **Retrofit**, **Coil**, and **ExoPlayer**

---

## 📸 Screenshots

_Add your screenshots here!_

---

## 🛠️ Tech Stack

- **Jetpack Compose** UI
- **Material 3** Design Components
- **Retrofit2** + **Gson** – API calls
- **Coil** – image loading
- **ExoPlayer** – in-app video playback
- **SharedPreferences** – persistent favorites
- **Navigation Compose** (optional)
- **TMDb API** – movie data

---

## 🔑 Setup Instructions

1. Get a free API key from [TMDb](https://www.themoviedb.org/settings/api)
2. Open the project in Android Studio
3. In `MovieViewModel.kt`, replace:

   ```kotlin
   private val apiKey = "YOUR_API_KEY"

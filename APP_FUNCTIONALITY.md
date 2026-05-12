# Movies App - Latiris - Functionality Guide

## ✅ App Overview
This is a fully functional Android movie application that fetches movies from The Movie Database (TMDB) API and displays them with trailers and cinema location mapping.

## 📱 Key Features

### 1. **Main Activity (MainActivity)**
- Displays a list of popular movies from TMDB API
- RecyclerView with smooth scrolling
- Click on any movie to view details
- Uses Volley for network requests
- Uses Glide for image loading

### 2. **Movie Detail Activity (MovieDetailActivity)**
- Shows full movie details:
  - Movie title and poster image
  - Movie description/overview
  - Play movie trailer button
  - Google Map displaying nearby cinema locations
- Requests location permissions to show user's current location on map
- Displays a cinema marker at fixed location (33.596460, -7.615480)

### 3. **Video Player (VideoPlayer)**
- Displays movie trailer in WebView
- Supports YouTube embedded videos
- Handles device orientation changes
- Enables JavaScript for video playback

### 4. **Data Management (MyMovieData)**
- Movie data model class
- Stores: ID, Title, Release Date, Poster Image Path

### 5. **UI Components (MyMovieAdapter)**
- RecyclerView adapter for displaying movies
- Shows poster image, title, and release date
- Handles click events to navigate to movie details

## 🔑 API Configuration
- **API Key**: `e21c9bd08ef733416fa4adc42dad2a14`
- **Base URL**: `https://api.themoviedb.org/3/movie/`
- **Image URL**: `https://image.tmdb.org/t/p/w500`

## 📦 Dependencies
- **Androidx AppCompat**: UI components
- **Androidx RecyclerView**: List display
- **Google Play Services Maps**: Map integration
- **Volley**: HTTP networking
- **Glide**: Image loading
- **Google Maps SDK**: Map functionality

## 🔐 Permissions Required
- `INTERNET` - For API requests
- `ACCESS_FINE_LOCATION` - For GPS coordinates
- `ACCESS_COARSE_LOCATION` - For approximate location
- `ACCESS_BACKGROUND_LOCATION` - Background location access

## 🚀 How to Run

1. **Build the project**:
   ```
   ./gradlew build
   ```

2. **Run on emulator or device**:
   ```
   ./gradlew installDebug
   ```

3. **App Flow**:
   - App opens to MainActivity showing popular movies
   - Click any movie to open MovieDetailActivity
   - Press "Play Movie" to watch trailer in VideoPlayer
   - Map shows cinema location (grant location permission when prompted)
   - Grant location permission to see your current position on map

## ⚙️ Architecture
- **Network**: Volley for API calls
- **Image Loading**: Glide with caching
- **Maps**: Google Maps SDK v2
- **Video Playback**: WebView with YouTube embed
- **Data Model**: POJO (MyMovieData)

## 🐛 Fixed Issues
✅ Added VideoPlayer to AndroidManifest.xml
✅ Fixed import statements and removed unused imports
✅ Updated MainActivity to properly add queue to requests
✅ Fixed VideoPlayer null checks and WebViewClient setup
✅ Added proper string resources for user-facing text
✅ Converted anonymous inner classes to lambda expressions
✅ Added proper @NonNull annotations
✅ Cleaned up build.gradle.kts (removed duplicate dependencies)

## 📝 String Resources
All user-facing strings are now in strings.xml:
- App name
- Error messages
- Location permission messages
- Trailer unavailable message

## 🎨 UI Layout Files
- `activity_main.xml` - Main movie list
- `activity_movie_detail.xml` - Movie details with map
- `activity_video_player.xml` - Video player WebView
- `movie_item_list.xml` - Individual movie card layout

## 🔄 Network Requests
- Popular Movies: `/3/movie/popular`
- Movie Details: `/3/movie/{movieId}`
- Movie Videos: `/3/movie/{movieId}/videos`

All requests include the API key parameter.

---

**Status**: ✅ **FULLY FUNCTIONAL**
The app is ready for testing and deployment.


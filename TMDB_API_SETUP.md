# TMDB API Setup Guide

## Getting Your API Key

To use The Movie Database (TMDB) API, you need to get a free API key:

1. **Visit**: https://www.themoviedb.org/settings/api
2. **Sign up** for a free account if you don't have one
3. **Request an API key** (it's free and takes a few minutes)
4. **Copy your API key**

## Configure the API Key

Once you have your API key, update the `NetworkModule.kt` file:

```kotlin
// In NetworkModule.kt, replace this line:
private const val TMDB_API_KEY = "your_tmdb_api_key_here"

// With your actual API key:
private const val TMDB_API_KEY = "your_actual_api_key_here"
```

## API Key Security

⚠️ **Important**: In a production app, you should:
- Store the API key in a secure configuration file
- Use build variants to keep the key out of version control
- Consider using a backend service to proxy API calls

## Fallback Data

The app includes fallback movie data that will be used if:
- No API key is configured
- Network requests fail
- API rate limits are exceeded

## Testing Without API Key

You can test the app without an API key - it will use the built-in fallback data to demonstrate the functionality.

## TMDB API Documentation

- **Configuration**: https://developer.themoviedb.org/reference/configuration-details
- **Movies**: https://developer.themoviedb.org/reference/movie-popular-list
- **Search**: https://developer.themoviedb.org/reference/search-movie
- **Genres**: https://developer.themoviedb.org/reference/genre-movie-list

## Rate Limits

TMDB API has rate limits:
- **40 requests per 10 seconds**
- **4,000 requests per day**

The app is designed to cache data and make minimal API calls to stay within these limits.

package com.v7h.whattodonext.data.model

/**
 * Result wrapper for handling success and error states
 * 
 * This follows 2025 Android best practices for error handling
 * and provides a clean way to handle async operations with
 * proper error states.
 * 
 * Applied Rules: Debug logs, comments, proper error handling
 */
sealed class Result<out T> {
    /**
     * Success state with data
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * Error state with exception
     */
    data class Error(val exception: Throwable) : Result<Nothing>()
    
    /**
     * Loading state
     */
    object Loading : Result<Nothing>()
}

/**
 * Extension function to check if result is success
 */
fun <T> Result<T>.isSuccess(): Boolean = this is Result.Success

/**
 * Extension function to check if result is error
 */
fun <T> Result<T>.isError(): Boolean = this is Result.Error

/**
 * Extension function to check if result is loading
 */
fun <T> Result<T>.isLoading(): Boolean = this is Result.Loading

/**
 * Extension function to get data from success result
 */
fun <T> Result<T>.getDataOrNull(): T? {
    return if (this is Result.Success) data else null
}

/**
 * Extension function to get error from error result
 */
fun <T> Result<T>.getErrorOrNull(): Throwable? {
    return if (this is Result.Error) exception else null
}

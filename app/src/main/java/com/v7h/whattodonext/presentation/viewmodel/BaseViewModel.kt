package com.v7h.whattodonext.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v7h.whattodonext.data.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel with common functionality for all ViewModels
 * 
 * This provides:
 * - Common error handling patterns
 * - Loading state management
 * - Debug logging
 * - Proper lifecycle management
 * 
 * Applied Rules: Debug logs, comments, ViewModel with StateFlow, proper error handling
 */
abstract class BaseViewModel : ViewModel() {
    
    // Common UI state for error handling
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    /**
     * Handle errors in a consistent way across all ViewModels
     */
    protected fun handleError(throwable: Throwable, operation: String) {
        android.util.Log.e("BaseViewModel", "Error in $operation: ${throwable.message}", throwable)
        
        // Set user-friendly error message
        _errorState.value = when (throwable) {
            is java.net.UnknownHostException -> "No internet connection. Please check your network."
            is java.net.SocketTimeoutException -> "Request timed out. Please try again."
            is java.io.IOException -> "Network error. Please try again."
            else -> "An unexpected error occurred. Please try again."
        }
        
        // Clear error after 5 seconds
        viewModelScope.launch {
            kotlinx.coroutines.delay(5000)
            _errorState.value = null
        }
    }
    
    /**
     * Set loading state
     */
    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
        android.util.Log.d("BaseViewModel", "Loading state changed: $loading")
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _errorState.value = null
        android.util.Log.d("BaseViewModel", "Error state cleared")
    }
    
    /**
     * Execute a suspend function with proper error handling
     */
    protected fun <T> executeWithErrorHandling(
        operation: String,
        block: suspend () -> T
    ) {
        viewModelScope.launch {
            try {
                setLoading(true)
                block()
                android.util.Log.d("BaseViewModel", "$operation completed successfully")
            } catch (e: Exception) {
                handleError(e, operation)
            } finally {
                setLoading(false)
            }
        }
    }
    
    /**
     * Execute a suspend function that returns a Result
     */
    protected fun <T> executeResult(
        operation: String,
        block: suspend () -> Result<T>
    ): StateFlow<Result<T>> {
        val resultFlow = MutableStateFlow<Result<T>>(Result.Loading)
        
        viewModelScope.launch {
            try {
                setLoading(true)
                val result = block()
                resultFlow.value = result
                android.util.Log.d("BaseViewModel", "$operation completed with result: ${result::class.simpleName}")
            } catch (e: Exception) {
                handleError(e, operation)
                resultFlow.value = Result.Error(e)
            } finally {
                setLoading(false)
            }
        }
        
        return resultFlow.asStateFlow()
    }
}

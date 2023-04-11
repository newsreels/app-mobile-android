package com.ziro.bullet.common.base

open class UiState<out T> {
    object Loading: UiState<Nothing>()
    data class Success<T>(val data: T? = null): UiState<T>()
    data class Error(val error: Throwable?): UiState<Nothing>()

    var isEmpty = true
}
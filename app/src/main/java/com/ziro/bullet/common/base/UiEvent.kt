package com.ziro.bullet.common.base

open class UiEvent {
    data class Loading(val isLoading: Boolean): UiEvent()
    data class Error(val error: Throwable): UiEvent()
    object Success: UiEvent()
}
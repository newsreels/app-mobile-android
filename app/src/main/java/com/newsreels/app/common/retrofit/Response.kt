package com.newsreels.app.common.retrofit

open class Response<T> {
    data class Success<T>(val data: T): Response<T>()
    data class Error(val error: Throwable): Response<Nothing>()
}
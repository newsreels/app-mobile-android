package com.ziro.bullet.common.retrofit

open class Response<T> {
    data class Success<T>(val data: T): Response<T>()
    data class Error(val error: Throwable): Response<Nothing>()
}
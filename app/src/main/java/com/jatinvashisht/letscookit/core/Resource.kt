package com.jatinvashisht.letscookit.core

sealed class Resource<T>(val data: T?, val error: String?){
    class Loading<T> : Resource<T>(null, null)
    class Success<T>(data: T): Resource<T>(data = data, error = null)
    class Error<T>(error: String): Resource<T>(data = null, error = error)
}
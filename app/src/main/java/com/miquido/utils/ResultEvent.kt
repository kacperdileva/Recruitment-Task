package com.miquido.utils

sealed class ResultEvent {
    object Success: ResultEvent()
    data class Error(val msg: String): ResultEvent()
}
package com.jatinvashisht.letscookit.domain.pagination

interface Paginator <Key, Item> {
    suspend fun loadNextItems()
    suspend fun reset()
}
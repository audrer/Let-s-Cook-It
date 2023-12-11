package com.jatinvashisht.letscookit.data.remote.dto.recipes

data class Ingredient(
    val description: String = "",
    val quantity: String = ""
){
    override fun toString(): String {
        return "$quantity $description"
    }
}
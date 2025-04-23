package ru.skorobogatov.t_investsendbox.data.settings

interface TokenInterface {

    fun saveToken(token: String)

    fun getToken(): String?

    fun checkToken(): Boolean
}
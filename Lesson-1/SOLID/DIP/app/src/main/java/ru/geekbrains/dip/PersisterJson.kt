package ru.geekbrains.dip

import com.google.gson.Gson

class PersisterJson : PersisterUserable {
    override fun convert(user: Userable): String {
        val gson = Gson()
        return gson.toJson(user)
    }
}
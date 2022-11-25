package ru.geekbrains.dip

interface PersisterUserable {
    fun convert(user: Userable): String
}
package ru.geekbrains.dip

class User(
    override var name: String,
    override var fam: String,
    override var age: String,
    override var email: String
) : Userable
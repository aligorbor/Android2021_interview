package ru.geekbrains.srp

import kotlin.math.PI

class Circle(private val radius: Int) {
    val area: Double
        get() {
            return PI * radius * radius
        }

    val circumference: Double
        get() {
            return PI * radius * 2
        }
}
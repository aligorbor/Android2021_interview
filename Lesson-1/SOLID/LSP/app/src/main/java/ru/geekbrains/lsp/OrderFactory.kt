package ru.geekbrains.lsp

class OrderFactory {
    fun create(quantity: Int, price: Double): Orderable{
        return Order(quantity, price)
    }
}
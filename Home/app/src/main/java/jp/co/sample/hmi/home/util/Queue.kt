package jp.co.sample.hmi.home.util

class Queue<T> {

    private val list = mutableListOf<T>()

    private var position = 0

    fun contains(element: T): Boolean {
        val subList = list.subList(position, list.size)
        return subList.contains(element)
    }

    fun containsSoFar(element: T): Boolean {
        return list.contains(element)
    }

    fun isEmpty(): Boolean {
        return this.size == 0
    }

    fun clear() {
        list.clear()
        position = 0
    }

    fun push(element: T): Boolean {
        return list.add(element)
    }

    fun pushIfNotPushedBefore(element: T): Boolean {
        if (containsSoFar(element)) {
            return false
        } else {
            return list.add(element)
        }
    }

    fun peek(): T {
        return list[position]
    }

    fun pop(): T? {
        var element: T? = null
        if (!isEmpty()) {
            element = list[position]
            position++
        }
        return element
    }

    val size: Int
        get() = (list.size - position)

}
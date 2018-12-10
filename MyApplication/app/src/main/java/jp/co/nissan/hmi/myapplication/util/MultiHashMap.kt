package jp.co.nissan.hmi.myapplication.util

class MultiHashMap<K, V> : HashMap<K, MutableSet<V>> {

    constructor() {}

    constructor(size: Int) : super(size) {}

    fun addToList(key: K, value: V) {
        var sets: MutableSet<V>? = get(key)
        if (sets == null) {
            sets = mutableSetOf()
            sets.add(value)
            put(key, sets)
        } else {
            sets.add(value)
        }
    }

    override fun clone(): MultiHashMap<K, V> {
        val map = MultiHashMap<K, V>(size)
        for ((key, value) in entries) {
            for (v in value) {
                map.addToList(key, v)
            }
        }
        return map
    }
}

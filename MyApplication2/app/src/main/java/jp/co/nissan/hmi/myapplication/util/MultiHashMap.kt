package jp.co.nissan.hmi.myapplication.util

class MultiHashMap<K, V> : HashMap<K, MutableList<V>> {

    constructor() {}

    constructor(size: Int) : super(size) {}

    fun addToList(key: K, value: V) {
        var list: MutableList<V>? = get(key)
        if (list == null) {
            list = mutableListOf()
            list.add(value)
            put(key, list)
        } else {
            list.add(value)
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

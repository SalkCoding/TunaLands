package com.salkcoding.tunalands.util

class ObservableMap<K, V>(private val map: MutableMap<K, V>, private val onChange: Observed<K, V>) : MutableMap<K, V> by map {
    // map 에 변화가 생기면 changed() 함수가 호출됨

    override fun clear() {
        map.clear()
        changed()
    }

    override fun put(key: K, value: V): V? {
        val res = map.put(key, value)
        changed()
        return res;
    }

    override fun putAll(from: Map<out K, V>) {
        map.putAll(from)
        changed()
    }

    override fun remove(key: K): V? {
        val res = map.remove(key)
        changed()
        return res;
    }

    private fun changed() {
        onChange.run(map)
    }

    interface Observed<K, V> {
        fun run(newMap: MutableMap<K, V>)
    }
}
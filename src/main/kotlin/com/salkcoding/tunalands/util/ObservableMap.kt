package com.salkcoding.tunalands.util

import com.salkcoding.tunalands.TunaLands
import com.salkcoding.tunalands.tunaLands
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.*

class ObservableMap<K, V>(
    private val plugin: TunaLands,
    private val map: MutableMap<K, V>,
    private val onChange: Observed<K, V>
) : MutableMap<K, V> by map {
    // map 에 변화가 생기면 changed() 함수가 호출됨

    private val task: BukkitTask

    init {
        // 주기적으로 업데이트
        // 코드가 스파게티지만 어쩔 수 없었음
        task = Bukkit.getScheduler().runTaskTimer(
            tunaLands,
            Runnable {
                this.sync()
            },
            100 + Random().nextLong(100),
            100
        )
    }

    fun disable(){
        task.cancel()
    }

    override fun clear() {
        map.clear()
        sync()
    }

    override fun put(key: K, value: V): V? {
        val res = map.put(key, value)
        sync()
        return res
    }

    override fun putAll(from: Map<out K, V>) {
        map.putAll(from)
        sync()
    }

    override fun remove(key: K): V? {
        val res = map.remove(key)
        sync()
        return res
    }

    fun sync() {
        onChange.syncChanges(map)
    }

    interface Observed<K, V> {
        fun syncChanges(newMap: MutableMap<K, V>)
    }
}
package com.salkcoding.tunalands.bungee

import com.google.gson.JsonObject
import com.salkcoding.tunalands.metamorphosis
import java.util.concurrent.BlockingQueue

class BroadcastLandMembersRunnable(
    val queue: BlockingQueue<String>
) : Runnable {
    override fun run() {
        val messages: MutableList<String> = mutableListOf()
        try {
            queue.drainTo(messages)
            var i = 0
            var message = ""
            messages.forEach { msg ->
                if (i % 50 == 0) {
                    if (i > 0){
                        val jsonMessage: JsonObject = JsonObject().apply {
                            this.addProperty("mapString", message)
                        }
                        metamorphosis.send("com.salkcoding.tunalands.update_land_member_change_bulk", jsonMessage.toString())
                    }
                    message = msg
                } else {
                    message += "&$msg"
                }
                i++
            }
            if (message.isNotEmpty()){
                val jsonMessage: JsonObject = JsonObject().apply {
                    this.addProperty("mapString", message)
                }
                metamorphosis.send("com.salkcoding.tunalands.update_land_member_change_bulk", jsonMessage.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
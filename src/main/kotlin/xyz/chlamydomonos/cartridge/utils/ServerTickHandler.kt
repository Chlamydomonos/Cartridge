package xyz.chlamydomonos.cartridge.utils

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.ServerTickEvent
import java.util.*

@EventBusSubscriber
object ServerTickHandler {
    private class Task(
        val id: Int,
        val func: () -> Unit,
        val time: Int
    )

    private var currentTime = 0
    private var counter = 0

    private val taskQueue = PriorityQueue(compareBy(Task::time).thenBy { it.id })

    fun addTask(delayTicks: Int, task: () -> Unit) {
        taskQueue.add(Task(counter++, task, currentTime + delayTicks))
    }

    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent.Post) {
        currentTime = event.server.tickCount

        while (!taskQueue.isEmpty() && taskQueue.peek().time <= currentTime) {
            val task = taskQueue.poll()
            task.func()
        }
    }
}
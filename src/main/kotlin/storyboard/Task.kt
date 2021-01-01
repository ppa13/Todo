package storyboard

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

//@Serializable
data class Task(
    override var name: String,
    override var description: String = "",
) : BoardItem {
    override var state: ItemState = ItemState.NEW
    override var priority: Int = 0
        set(value) {
            field = value.apply {
                if (this > 10) throw IllegalArgumentException("Priority must be smaller or equal 10")
                if (this < 0) throw IllegalArgumentException("Priority must be larger or equal 0")
            }
        }
    override var stackRank: Long = 0L
        set(value) {
            field = value.apply {
                if (this < 0) throw IllegalArgumentException("Priority must be larger or equal 0")
            }
        }
    override var effortEstimationInHours: Int = 0
        set(value) {
            field = value.apply {
                if (this < 0) throw IllegalArgumentException("Priority must be larger or equal 0")
            }
        }
    override var creationDate: LocalDateTime? = null
    override var deliveryDate: LocalDateTime? = null
    override var lastModificationDate: LocalDateTime? = null
    override var dependencies: MutableSet<Long> = mutableSetOf()
    override var id: Long? = null

    fun addDependency(task: Task) {
        task?.id.apply { dependencies.add(this!!) }
    }

    fun addDependencies(tasks: List<Task>) {
        tasks.forEach { addDependency(it) }
    }

    fun removeDependency(task: Task) {
        dependencies.remove(task.id)
    }

    fun removeDependencies(tasks: List<Task>) {
        tasks.forEach { removeDependency(it) }
    }


}
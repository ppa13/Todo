package storyboard

import java.time.LocalDateTime
import java.util.*

interface BoardItem{
    var name: String
    var description: String
    var state: ItemState
    var priority: Int
    var stackRank: Long
    var effortEstimationInHours : Int
    var creationDate: LocalDateTime?
    var deliveryDate: LocalDateTime?
    var lastModificationDate: LocalDateTime?
    var dependencies: MutableSet<Long>
    val id: Long?
}
package storyboard

enum class ItemState(displayName: String) {
    NEW("new"),
    CLOSED("closed"),
    IN_WORK("in work"),
    FIXED("fixed"),
    REDUNDANT("redundant"),
    REMOVED("removed"),
    DUPLICATE("duplicate"),
}
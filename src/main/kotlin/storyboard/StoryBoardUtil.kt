package storyboard

import java.time.format.DateTimeFormatter

class StoryBoardUtil {
    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss:SSS")
    }
}
package server

import db
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*
import storyboard.ItemState
import storyboard.Task
import storyboard.TaskDataBase
import java.io.File

private fun BODY.print() {
    val myDiv = h1 {
        classes = setOf("text-danger")
        p {
            classes = setOf("text-danger")
            +"Here is "
            a(href = "https://kotlinlang.org") {
                +"official Kotlin site"
            }
        }
    }
    val tasks =
        db.stmt.executeQuery("SELECT COUNT(*) FROM ${TaskDataBase.TableInfo.TABLE_NAME}").getString(1).toInt()
    div {
        +"Number of Items: ${db.stmt.execute(db.selectAll())}"
        ul {
            +"names:"
            for (i in 1..tasks) {
                li {
                    +"${db.getTask(i).lastModificationDate}"
                }
            }
        }
    }
}


fun Routing.routes() {
    get("/") {
        call.respondFile(File("resources/html/index.html"))
    }
    get("/js/item.js") {
        call.respondFile(File("resources/html/js/item.js"))
    }
    get("/foo") {
        val task = db.getTask(db.numberOfTasks())
        call.respond(task)
    }
}

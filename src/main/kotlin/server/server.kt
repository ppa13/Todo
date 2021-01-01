import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.serialization.json
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import server.routes
import storyboard.TaskDataBase

val db = TaskDataBase()

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(Routing) {
        routes()
    }
}


fun main() {
    embeddedServer(
        Netty,
        8080,
        watchPaths = listOf("BlogAppKt"),
        module = Application::module,
    ).start()
}

fun initDataBase() {

}
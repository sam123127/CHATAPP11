import java.io.*
import java.net.*
import kotlin.concurrent.thread

class ChatClient(private val host: String, private val port: Int) {
    private lateinit var socket: Socket
    private lateinit var out: PrintWriter

    fun start() {
        socket = Socket(host, port)
        out = PrintWriter(socket.getOutputStream(), true)

        thread { receiveMessages() }

        val userInput = BufferedReader(InputStreamReader(System.`in`))
        var message: String?
        while (true) {
            message = userInput.readLine() ?: break
            if (message == "/exit") {
                out.println("/exit")
                break
            }
            out.println(message)
        }

        socket.close()
    }

    private fun receiveMessages() {
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))
        var message: String?
        while (true) {
            message = input.readLine() ?: break
            println(message)
        }
    }
}

fun main() {
    val client = ChatClient(host = "127.0.0.1", port = 12345)
    client.start()
}

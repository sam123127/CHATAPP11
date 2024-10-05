import java.io.*
import java.net.*
import kotlin.concurrent.thread

class ChatServer(private val port: Int) {
    private val clients = mutableListOf<ClientHandler>()

    fun start() {
        val serverSocket = ServerSocket(port)
        println("Chat server started on port $port")

        while (true) {
            val socket = serverSocket.accept()
            val clientHandler = ClientHandler(socket, this)
            clients.add(clientHandler)
            thread { clientHandler.handle() }
        }
    }

    fun broadcast(message: String, sender: ClientHandler) {
        clients.forEach { client ->
            if (client != sender) {
                client.sendMessage(message)
            }
        }
    }

    fun removeClient(client: ClientHandler) {
        clients.remove(client)
    }
}

class ClientHandler(private val socket: Socket, private val server: ChatServer) {
    private val out: PrintWriter = PrintWriter(socket.getOutputStream(), true)
    private lateinit var username: String

    fun handle() {
        try {
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            out.println("Enter your username:")
            username = input.readLine()
            server.broadcast("$username has joined the chat.", this)

            var message: String?
            while (true) {
                message = input.readLine() ?: break
                if (message == "/exit") {
                    break
                }
                server.broadcast("$username: $message", this)
            }
        } finally {
            server.removeClient(this)
            server.broadcast("$username has left the chat.", this)
            socket.close()
        }
    }

    fun sendMessage(message: String) {
        out.println(message)
    }
}

fun main() {
    val server = ChatServer(port = 12345)
    server.start()
}

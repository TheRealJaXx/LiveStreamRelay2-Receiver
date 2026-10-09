package com.example.livestreamrelayreceiver.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import java.io.DataInputStream
import java.io.IOException
import java.net.Socket
import kotlin.concurrent.thread

class MjpegStreamClient(
    private val host: String,
    private val port: Int = 8888,
    private val onFrame: (Bitmap) -> Unit,
    private val onStatus: (String) -> Unit,
    private val onError: (Throwable) -> Unit
) {
    @Volatile
    private var running = false

    @Volatile
    private var socket: Socket? = null

    fun start() {
        if (running) return
        running = true

        thread(start = true, name = "mjpeg-client") {
            try {
                onStatus("Connecting to $host:$port...")
                val clientSocket = Socket(host, port)
                socket = clientSocket
                onStatus("Connected to $host:$port")

                val input = DataInputStream(clientSocket.getInputStream())
                var frameCount = 0L
                var lastFpsReset = SystemClock.elapsedRealtime()

                while (running && !clientSocket.isClosed) {
                    val length = input.readInt()
                    if (length <= 0 || length > 10_000_000) {
                        throw IOException("Invalid frame length: $length")
                    }

                    val buffer = ByteArray(length)
                    var offset = 0
                    while (offset < length) {
                        val read = input.read(buffer, offset, length - offset)
                        if (read == -1) {
                            throw IOException("Stream ended before full frame was received")
                        }
                        offset += read
                    }

                    val bitmap = BitmapFactory.decodeByteArray(buffer, 0, length)
                    if (bitmap != null) {
                        onFrame(bitmap)
                        frameCount += 1

                        val now = SystemClock.elapsedRealtime()
                        if (now - lastFpsReset >= 1000L) {
                            val fps = frameCount * 1000f / (now - lastFpsReset)
                            onStatus("Streaming • %.1f fps".format(fps))
                            frameCount = 0L
                            lastFpsReset = now
                        }
                    } else {
                        onStatus("Received invalid MJPEG frame")
                    }
                }
            } catch (e: Exception) {
                if (running) {
                    onStatus("Connection lost")
                    onError(e)
                }
            } finally {
                running = false
                try {
                    socket?.close()
                } catch (_: Exception) {}
                socket = null
            }
        }
    }

    fun stop() {
        running = false
        try {
            socket?.close()
        } catch (_: Exception) {}
        socket = null
    }
}

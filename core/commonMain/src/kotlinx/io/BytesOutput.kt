package kotlinx.io

import kotlinx.io.buffer.*
import kotlin.contracts.*




class BytesOutput(bufferSize: Int = DEFAULT_BUFFER_SIZE) : Output(bufferSize) {
    private val bytes = Bytes()

    val size: Int = TODO()

    fun createInput(): BytesInput {
        TODO()
    }

    override fun flush(source: Buffer, length: Int) {
        bytes.append(source, length)
    }

    override fun close() {
        flush()
    }
}

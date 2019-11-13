package kotlinx.io

import kotlinx.io.buffer.*
import kotlin.contracts.*

/**
 * Create [Bytes] with [bufferSize] and fills it from [builder].
 */
fun buildInput(bufferSize: Int = DEFAULT_BUFFER_SIZE, block: BytesOutput.() -> Unit): BytesInput {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return BytesOutput(bufferSize).apply {
        block()
    }.createInput()
}
package kotlinx.io

import kotlinx.io.buffer.*

class BytesInput internal constructor(
    private val bytes: Bytes
) : Input(bytes, DefaultBufferPool.Unmanaged) {

    val remaining: Int = TODO()

    override fun fill(buffer: Buffer): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeSource() {
    }
}
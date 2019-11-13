package kotlinx.io

import kotlinx.io.buffer.*

internal typealias BytesPointer = Int

internal class Bytes {
    private var buffers: Array<Buffer?> = arrayOfNulls(initialPreviewSize)

    /**
     * Limit of each [Buffer] in [buffers].
     */
    private var limits: IntArray = IntArray(initialPreviewSize)

    /**
     * Index of the first [Buffer] in [buffers].
     */
    private var head: Int = 0

    /**
     * Index of the last [Buffer] in [buffers].
     */
    private var tail: Int = 0

    /**
     * Calculate size of [Bytes].
     */
    fun size(): Int = size(StartPointer)

    /**
     * Check if [Bytes] is empty.
     */
    fun isEmpty(): Boolean = tail == head

    /**
     * Create [BytesInput] from this.
     */
    fun createInput(): BytesInput = BytesInput(this)

    override fun toString() = "Bytes($head..$tail)"

    fun append(buffer: Buffer, limit: Int) {
        if (head > 0) {
            // if we are appending buffers after some were discarded, 
            // compact arrays so we can store more without allocations
            buffers.copyInto(buffers, 0, head, tail)
            limits.copyInto(limits, 0, head, tail)
            tail -= head
            head = 0
        }

        if (tail == buffers.size) {
            buffers = buffers.copyInto(arrayOfNulls(buffers.size * 2))
            limits = limits.copyInto(IntArray(buffers.size * 2))
        }

        buffers[tail] = buffer
        limits[tail] = limit
        tail++
    }

    fun discardFirst(): Buffer? {
        if (head == tail) {
            return null
        }

        val result = buffers[head]!!

        buffers[head] = null
        limits[head] = -1
        head++

        return result
    }

    inline fun pointed(pointer: BytesPointer, consumer: (Int) -> Unit): Buffer {
        // Buffer is returned and not sent to `consumer` because we need to initialize field in Input's constructor
        val index = pointer + head
        val buffer = buffers[index] ?: throw NoSuchElementException("There is no buffer at pointer $pointer")
        val limit = limits[index]
        consumer(limit)
        return buffer
    }

    fun limit(pointer: BytesPointer): Int = limits[pointer + head]

    fun advancePointer(pointer: BytesPointer): BytesPointer = pointer + 1

    fun isAfterLast(index: BytesPointer) = head + index >= tail

    fun size(pointer: BytesPointer): Int {
        // ???: if Input.ensure operations are frequent enough, consider storing running size in yet another int array
        var sum = 0
        for (index in (pointer + head) until tail) {
            sum += limits[index]
        }

        return sum
    }

    companion object {
        internal const val InvalidPointer = Int.MIN_VALUE
        internal const val StartPointer = 0

        private const val initialPreviewSize = 1
    }
}

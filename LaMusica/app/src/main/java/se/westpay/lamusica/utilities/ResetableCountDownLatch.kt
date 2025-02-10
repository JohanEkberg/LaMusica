package se.westpay.lamusica.utilities

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ResetableCountDownLatch(private val initialCount: Int) {
    @Volatile
    private var _latch: CountDownLatch

    init {
        _latch = CountDownLatch(initialCount)
    }

    fun reset() {
        _latch = CountDownLatch(initialCount)
    }

    fun countDown() {
        _latch.countDown()
    }

    @Throws(InterruptedException::class)
    fun await() {
        _latch.await()
    }

    @Throws(InterruptedException::class)
    fun await(timeout: Long, unit: TimeUnit?): Boolean {
        return _latch.await(timeout, unit)
    }
}
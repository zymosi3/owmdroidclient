package com.zymosi3.util

/**
 * Common util functions
 */

/**
 * Current thread name.
 */
public fun threadName():String {
    return Thread.currentThread().name
}

/**
 * Helper method for logcat prints current thread name as "Thread : <threadName>".
 */
public fun logThreadName():String {
    return "Thread: ${threadName()}.";
}

/**
 * Tries to do action n times. In case of any exception sleeps the given interval and tries again.
 */
public fun <Result> retry(n: Int, sleepMs: Int, action: (tryN: Int) -> Result): Result {
    for (i in 1..n) {
        try {
            return action(i)
        } catch (t: Throwable) {
            if (i == n) {
                throw t
            }
            Thread.sleep(sleepMs.toLong())
        }
    }
    throw RuntimeException("That should never happen")
}

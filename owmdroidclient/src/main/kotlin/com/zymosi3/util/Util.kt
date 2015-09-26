package com.zymosi3.util

/**
 * Common util functions
 */

public fun threadName():String {
    return Thread.currentThread().name
}

public fun logThreadName():String {
    return "Thread: ${threadName()}.";
}

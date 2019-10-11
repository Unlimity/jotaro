package com.unlimity.jotaro

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

fun <T> KProperty0<T>.asDeferred(scope: CoroutineScope): Deferred<T> {
    return scope.asDeferred(this)
}

fun <T> KMutableProperty0<T>.asDeferred(scope: CoroutineScope, value: T): Deferred<Unit> {
    return scope.asDeferred(this, value)
}

fun <T> CoroutineScope.asDeferred(property: KProperty0<T>): Deferred<T> {
    return this.async(start = CoroutineStart.LAZY) {
        property.get()
    }
}

fun <T> CoroutineScope.asDeferred(property: KMutableProperty0<T>, value: T): Deferred<Unit> {
    return this.async(start = CoroutineStart.LAZY) {
        property.set(value)
    }
}

package com.unlimity.jotaro

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlin.reflect.KProperty0

fun <T> KProperty0<T>.asDeferred(scope: CoroutineScope): Deferred<T> {
    return scope.asDeferred(this)
}

fun <T> CoroutineScope.asDeferred(property: KProperty0<T>): Deferred<T> {
    return this.async(start = CoroutineStart.LAZY) {
        property.get()
    }
}

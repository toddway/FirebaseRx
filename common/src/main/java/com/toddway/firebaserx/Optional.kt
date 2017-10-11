package com.toddway.firebaserx

class Optional<out T>(private val optional: T?) {

    val isEmpty: Boolean
        get() = this.optional == null

    fun get(): T? {
        return optional
    }
}
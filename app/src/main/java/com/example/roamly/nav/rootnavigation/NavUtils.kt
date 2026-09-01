package com.example.roamly.nav.rootnavigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

fun <T : NavKey> NavBackStack<T>.push(route: T) {
    if (lastOrNull() != route) {
        add(route)
    }
}

fun <T : NavKey> NavBackStack<T>.pop(): Boolean {
    if (size <= 1) return false

    removeLastOrNull()
    return true
}

fun <T : NavKey> NavBackStack<T>.popIfCurrent(route: T): Boolean {
    if (lastOrNull() != route) return false

    return pop()
}

fun <T : NavKey> NavBackStack<T>.replace(route: T) {
    removeLastOrNull()
    push(route)
}

fun <T : NavKey> NavBackStack<T>.setRoot(root: T) {
    clear()
    add(root)
}

fun <T : NavKey> NavBackStack<T>.popTo(route: T): Boolean {
    val routeIndex = indexOfLast { it == route }

    if (routeIndex < 0) return false

    while (lastIndex > routeIndex) {
        removeLastOrNull()
    }

    return true
}
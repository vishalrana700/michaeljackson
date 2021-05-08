package com.song.michaeljackson.retrofit

import java.io.IOException

class NoConnectivityException : IOException() {
    // You can send any message whatever you want from here.
    override val message: String
        get() = "No internet connection"
    // You can send any message whatever you want from here.
}
package com.example.hmi.audio.common

enum class RepeatMode {

    NONE {
        override fun increment() = RepeatMode.REPEAT_ONE
    },

    REPEAT_ONE {
        override fun increment() = RepeatMode.REPEAT
    },

    REPEAT {
        override fun increment() = RepeatMode.NONE
    };

    abstract fun increment(): RepeatMode
}
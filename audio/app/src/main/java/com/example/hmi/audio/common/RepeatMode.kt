package com.example.hmi.audio.common

/**
 * The mode of repeat for playing song
 */
enum class RepeatMode {

    NONE {
        override fun increment() = RepeatMode.REPEAT_ONE
    },

    REPEAT_ONE {
        override fun increment() = RepeatMode.REPEAT
    },

    REPEAT {
        override fun increment() = RepeatMode.REPEAT_ALL
    },

    REPEAT_ALL {
        override fun increment() = RepeatMode.NONE
    };

    abstract fun increment(): RepeatMode
}
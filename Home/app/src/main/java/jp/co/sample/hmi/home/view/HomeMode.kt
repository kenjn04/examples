package jp.co.sample.hmi.home.view

enum class HomeMode {
    DISPLAY,

    REARRANGEMENT,

    SELECTION
}
interface HomeModeChangeListener {

    fun onHomeModeChanged(mode: HomeMode)
}


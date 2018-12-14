package jp.co.nissan.hmi.myapplication.drag

import jp.co.nissan.hmi.myapplication.Launcher

class DragController(val launcher: Launcher) {

    private val listeners = mutableListOf<DragListener>()

    interface DragListener {
        fun onDragStart()

        fun onDragEnd()
    }

    fun startDrag() {
        callOnDragStart()
    }

    private fun callOnDragStart() {
        for (listener in listeners) {
            listener.onDragStart()
        }
    }

    fun addDragListener(listener: DragListener) {
        listeners.add(listener)
    }

}
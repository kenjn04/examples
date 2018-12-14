package jp.co.nissan.hmi.myapplication.drag

import android.view.View
import jp.co.nissan.hmi.myapplication.Launcher
import jp.co.nissan.hmi.myapplication.widgethost.WidgetHostViewLoader

class PendingItemDragHelper(val view: View) {

    fun startDrag() {
        val launcher = Launcher.getLauncher(view.context)!!
        launcher.dragController.addDragListener(WidgetHostViewLoader(launcher, view))
        launcher.dragController.startDrag()
    }
}
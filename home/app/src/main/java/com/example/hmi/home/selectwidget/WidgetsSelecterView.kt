package com.example.hmi.home.selectwidget

import android.content.Context
import android.graphics.Point
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.hmi.home.Launcher
import com.example.hmi.home.R

/**
 * The widgets list view container.
 */
class WidgetsSelecterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseContainerView(context, attrs, defStyleAttr), View.OnLongClickListener, View.OnClickListener, DragSource {

    /* Global instances that are used inside this container. */
//    @Thunk
//    internal var mLauncher: Launcher
    private val launcher: Launcher

    /* Recycler view related member variables */
    private var mRecyclerView: WidgetsRecyclerView? = null
    private val mAdapter: WidgetsListAdapter

    /* Touch handling related member variables. */
    private var mWidgetInstructionToast: Toast? = null

    val touchDelegateTargetView: View?
        get() = mRecyclerView

    val intrinsicIconScaleFactor: Float
        get() = 0f

    val isEmpty: Boolean
        get() = mAdapter.getItemCount() === 0

    init {
        launcher = Launcher.getLauncher(context)!!
        mAdapter = WidgetsListAdapter(this, this, context)
    }

    protected fun onFinishInflate() {
        super.onFinishInflate()
        mRecyclerView = getContentView().findViewById(R.id.widgets_list_view) as WidgetsRecyclerView
        mRecyclerView!!.setAdapter(mAdapter)
        mRecyclerView!!.setLayoutManager(LinearLayoutManager(getContext()))
    }

    //
    // Returns views used for launcher transitions.
    //

    fun scrollToTop() {
        mRecyclerView!!.scrollToPosition(0)
    }

    //
    // Touch related handling.
    //

    override fun onClick(v: View) {
        // TODO: Revisit later
        /*
        // When we have exited widget tray or are in transition, disregard clicks
        if (!launcher.isWidgetsViewVisible()
            || launcher.getWorkspace().isSwitchingState()
            || v !is WidgetCell
        )
            return
        */
        handleClick()
    }

    fun handleClick() {
        // Nothing to do
    }

    override fun onLongClick(v: View): Boolean {
        // When we have exited the widget tray, disregard long clicks
        return if (!launcher.isWidgetsViewVisible()) false else handleLongClick(v)
    }

    fun handleLongClick(v: View): Boolean {
        // Return early if this is not initiated from a touch
        if (!v.isInTouchMode) return false
        // When we  are in transition, disregard long clicks
        if (launcher.getWorkspace().isSwitchingState()) return false
        // Return if global dragging is not enabled
        return if (!launcher.isDraggingEnabled()) false else beginDragging(v)

    }

    private fun beginDragging(v: View): Boolean {
        if (v is WidgetCell) {
            if (!beginDraggingWidget(v as WidgetCell)) {
                return false
            }
        } else {
            Log.e(TAG, "Unexpected dragging view: $v")
        }

        // We don't enter spring-loaded mode if the drag has been cancelled
        if (launcher.getDragController().isDragging()) {
            // Go into spring loaded mode (must happen before we startDrag())
            launcher.enterSpringLoadedDragMode()
        }

        return true
    }

    private fun beginDraggingWidget(v: WidgetCell): Boolean {
        // Get the widget preview as the drag representation
        val image = v.findViewById(R.id.widget_preview) as WidgetImageView

        // If the ImageView doesn't have a drawable yet, the widget preview hasn't been loaded and
        // we abort the drag.
        if (image.getBitmap() == null) {
            return false
        }

        val loc = IntArray(2)
        launcher.getDragLayer().getLocationInDragLayer(image, loc)

        PendingItemDragHelper(v).startDrag(
            image.getBitmapBounds(), image.getBitmap().getWidth(), image.getWidth(),
            Point(loc[0], loc[1]), this, DragOptions()
        )
        return true
    }

    //
    // Drag related handling methods that implement {@link DragSource} interface.
    //

    fun supportsAppInfoDropTarget(): Boolean {
        return true
    }

    /*
     * Both this method and {@link #supportsFlingToDelete} has to return {@code false} for the
     * {@link DeleteDropTarget} to be invisible.)
     */
    fun supportsDeleteDropTarget(): Boolean {
        return false
    }

    fun onDropCompleted(
        target: View, d: DragObject, isFlingToDelete: Boolean,
        success: Boolean
    ) {
        if (isFlingToDelete || !success || target !== mLauncher.getWorkspace() &&
            target !is DeleteDropTarget && target !is Folder
        ) {
            // Exit spring loaded mode if we have not successfully dropped or have not handled the
            // drop in Workspace
            mLauncher.exitSpringLoadedDragModeDelayed(
                true,
                Launcher.EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT, null
            )
        }
        mLauncher.unlockScreenOrientation(false)

        if (!success) {
            d.deferDragViewCleanupPostAnimation = false
        }
    }

    /**
     * Initialize the widget data model.
     */
    fun setWidgets(model: MultiHashMap<PackageItemInfo, WidgetItem>) {
        mAdapter.setWidgets(model)
        mAdapter.notifyDataSetChanged()

        val loader = getContentView().findViewById(R.id.loader)
        if (loader != null) {
            (getContentView() as ViewGroup).removeView(loader)
        }
    }

    fun getWidgetsForPackageUser(packageUserKey: PackageUserKey): List<WidgetItem> {
        return mAdapter.copyWidgetsForPackageUser(packageUserKey)
    }

    fun fillInLogContainerData(v: View, info: ItemInfo, target: Target, targetParent: Target) {
        targetParent.containerType = ContainerType.WIDGETS
    }

    companion object {
        private val TAG = "WidgetsContainerView"
        private val LOGD = false
    }
}
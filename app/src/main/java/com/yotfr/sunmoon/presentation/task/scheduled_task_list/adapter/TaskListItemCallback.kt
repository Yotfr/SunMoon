package com.yotfr.sunmoon.presentation.task.scheduled_task_list.adapter

import android.graphics.*
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import java.lang.IllegalArgumentException
import kotlin.math.abs

class TaskListItemCallback(
    val onUncompletedItemTrashed: (Int) -> Unit,
    val onCompletedItemTrashed: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.ACTION_STATE_IDLE,
    ItemTouchHelper.RIGHT
) {
    private val iconBounds = Rect()
    private val backgroundRect = RectF()
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (viewHolder) {
            is ScheduledUncompletedTaskListAdapter.ScheduledUncompletedTaskListViewHolder -> {
                onUncompletedItemTrashed(viewHolder.bindingAdapterPosition)
            }
            is ScheduledCompletedTaskAdapter.CompletedTaskViewHolder -> {
                onCompletedItemTrashed(viewHolder.bindingAdapterPosition)
            }

        }
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (
            viewHolder.itemViewType == R.layout.item_scheduled_completed_task_header ||
                    viewHolder.itemViewType == R.layout.item_scheduled_task_footer
        ) {
            ItemTouchHelper.ACTION_STATE_IDLE
        } else {
            super.getSwipeDirs(recyclerView, viewHolder)
        }
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.4f

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView: View = viewHolder.itemView
        val typedValueInactive = TypedValue()
        itemView.context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimaryVariant,
            typedValueInactive,
            true
        )
        val inactiveColor = typedValueInactive.data

        val typedValueActive = TypedValue()
        itemView.context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary,
            typedValueActive,
            true
        )
        val activeColor = typedValueActive.data

        val layoutMargin = itemView.resources.getDimensionPixelSize(R.dimen.default_margin)

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (abs(dX) > layoutMargin) {
                if (abs(dX) < (itemView.width * 0.4f)) {
                    drawBackground(c, itemView, inactiveColor)
                    drawIcon(c, itemView)
                } else {
                    drawBackground(c, itemView, activeColor)
                    drawIcon(c, itemView)
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawIcon(canvas: Canvas, itemView: View) {
        val layoutMargin = itemView.resources.getDimensionPixelSize(R.dimen.default_margin)
        val icon = ContextCompat.getDrawable(itemView.context, R.drawable.ic_delete_large) ?: throw
        IllegalArgumentException("Not found icon")
        val iconTint = itemView.resources.getColor(R.color.background_color, itemView.context.theme)
        icon.setTint(iconTint)
        val margin = (itemView.bottom - itemView.top - icon.intrinsicHeight) / 2

        with(iconBounds) {
            left = itemView.left + layoutMargin
            top = itemView.top + margin
            right = itemView.left + icon.intrinsicWidth + layoutMargin
            bottom = itemView.bottom - margin
        }

        icon.bounds = iconBounds
        icon.draw(canvas)

    }

    private fun drawBackground(canvas: Canvas, itemView: View, bgColor: Int) {
        val layoutMargin = itemView.resources.getDimensionPixelSize(R.dimen.default_margin)
        with(backgroundRect) {
            left = itemView.left.toFloat()
            top = itemView.top.toFloat()
            right = itemView.right.toFloat() - layoutMargin
            bottom = itemView.bottom.toFloat()
        }
        with(backgroundPaint) {
            color = bgColor
        }
        canvas.drawRect(backgroundRect, backgroundPaint)
    }


}
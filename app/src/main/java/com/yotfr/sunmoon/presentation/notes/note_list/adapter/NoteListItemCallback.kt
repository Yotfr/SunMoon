package com.yotfr.sunmoon.presentation.notes.note_list.adapter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.presentation.utils.getColorFromAttr
import java.lang.IllegalArgumentException
import kotlin.math.abs

class NoteListItemCallback(
    val onTrashItemNote: (Int) -> Unit,
    val onArchiveItemNote: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.ACTION_STATE_IDLE,
    ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT,

    ) {
    private val iconBounds = Rect()
    private val backgroundRect = RectF()
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.RIGHT -> {
                onTrashItemNote(viewHolder.bindingAdapterPosition)
            }
            ItemTouchHelper.LEFT -> {
                onArchiveItemNote(viewHolder.bindingAdapterPosition)
            }

        }
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (
            viewHolder.itemViewType == R.layout.item_note_list_footer
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

        val layoutMargin = itemView.resources.getDimensionPixelSize(R.dimen.default_margin)

        val typedValueInactive = TypedValue()
        itemView.context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimaryContainer,
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

        val typedValueInactiveArchive = TypedValue()
        itemView.context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorSecondary,
            typedValueInactiveArchive,
            true
        )
        val inactiveColorArchive = typedValueInactiveArchive.data

        val typedValueActiveArchive = TypedValue()
        itemView.context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorSecondaryContainer,
            typedValueActiveArchive,
            true
        )
        val activeColorArchive = typedValueActiveArchive.data

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (abs(dX) > layoutMargin && dX > 0) {
                if (abs(dX) < (itemView.width * 0.4f)) {
                    drawDeleteBackground(c, itemView, inactiveColor)
                    drawDeleteIconWithText(c, itemView)
                } else {
                    drawDeleteBackground(c, itemView, activeColor)
                    drawDeleteIconWithText(c, itemView)
                }
            } else if (abs(dX) > layoutMargin && dX < 0) {
                if (abs(dX) > (itemView.width * 0.4f)) {
                    drawArchiveBackground(c, itemView, inactiveColorArchive)
                    drawArchiveIcon(c, itemView)
                } else {
                    drawArchiveBackground(c, itemView, activeColorArchive)
                    drawArchiveIcon(c, itemView)
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawDeleteIconWithText(canvas: Canvas, itemView: View) {
        val layoutMargin = itemView.resources.getDimensionPixelSize(R.dimen.default_margin)
        val layoutTextMargin = itemView.resources.getDimensionPixelSize(R.dimen.huge_margin)

        val icon = ContextCompat.getDrawable(itemView.context, R.drawable.ic_delete) ?: throw
        IllegalArgumentException("Not found icon")

        val tint =itemView.context.getColorFromAttr(com.google.android.material.R.attr.colorSurface)
        icon.setTint(tint)

        val margin = (itemView.bottom - itemView.top - icon.intrinsicHeight) / 2

        val text = itemView.resources.getString(R.string.delete)

        with(iconBounds) {
            left = itemView.left + layoutMargin
            top = itemView.top + margin
            right = itemView.left + icon.intrinsicWidth + layoutMargin
            bottom = itemView.bottom - margin
        }

        with(textPaint) {
            color = tint
            textSize = 40F
            textAlign = Paint.Align.CENTER
        }

        val textY =
            (itemView.top + itemView.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2)
        val textX = itemView.left + icon.intrinsicWidth + layoutTextMargin

        canvas.drawText(text, textX.toFloat(), textY, textPaint)
        icon.bounds = iconBounds
        icon.draw(canvas)
    }

    private fun drawArchiveIcon(canvas: Canvas, itemView: View) {
        val layoutMargin = itemView.resources.getDimensionPixelSize(R.dimen.default_margin)
        val layoutTextMargin = itemView.resources.getDimensionPixelSize(R.dimen.huge_margin)

        val icon = ResourcesCompat.getDrawable(
            itemView.resources, R.drawable.ic_archive_gesture,
            itemView.context.theme
        ) ?: throw
        IllegalArgumentException("Not found icon")

        val tint =itemView.context.getColorFromAttr(com.google.android.material.R.attr.colorSurface)
        icon.setTint(tint)

        val margin = (itemView.bottom - itemView.top - icon.intrinsicHeight) / 2

        val text = itemView.resources.getString(R.string.archive)

        with(iconBounds) {
            left = itemView.right - icon.intrinsicWidth - layoutMargin
            top = itemView.top + margin
            right = itemView.right - layoutMargin
            bottom = itemView.bottom - margin
        }

        with(textPaint) {
            color = tint
            textSize = 40F
            textAlign = Paint.Align.CENTER
        }

        val textY =
            (itemView.top + itemView.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2)
        val textX = itemView.right - icon.intrinsicWidth - layoutTextMargin

        canvas.drawText(text, textX.toFloat(), textY, textPaint)
        icon.bounds = iconBounds
        icon.draw(canvas)
    }

    private fun drawDeleteBackground(canvas: Canvas, itemView: View, bgColor: Int) {
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
        canvas.drawRoundRect(backgroundRect, 32F, 32F, backgroundPaint)
    }

    private fun drawArchiveBackground(canvas: Canvas, itemView: View, bgColor: Int) {
        val layoutMargin = itemView.resources.getDimensionPixelSize(R.dimen.default_margin)
        with(backgroundRect) {
            left = itemView.left.toFloat() + layoutMargin
            top = itemView.top.toFloat()
            right = itemView.right.toFloat()
            bottom = itemView.bottom.toFloat()
        }
        with(backgroundPaint) {
            color = bgColor
        }
        canvas.drawRoundRect(backgroundRect, 32F, 32F, backgroundPaint)
    }

}
package com.yotfr.sunmoon.presentation.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.navigation.FloatingWindow
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.R.*
import com.yotfr.sunmoon.R
import java.lang.ref.WeakReference
import java.util.regex.Pattern

// I need to get access on AbstractAppBarOnDestinationChangedListener in navController.addOn
// destination changed listener to not get memory leak, but this class is internal
abstract class AbstractAppBarOnDestinationChangedListener(
    private val context: Context,
    configuration: AppBarConfiguration
) : NavController.OnDestinationChangedListener {
    private val topLevelDestinations: Set<Int> = configuration.topLevelDestinations
    private val openableLayoutWeakReference = configuration.openableLayout?.run {
        WeakReference(this)
    }
    private var arrowDrawable: DrawerArrowDrawable? = null
    private var animator: ValueAnimator? = null

    protected abstract fun setTitle(title: CharSequence?)

    protected abstract fun setNavigationIcon(icon: Drawable?, @StringRes contentDescription: Int)

    @Suppress("DEPRECATION")
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination is FloatingWindow) {
            return
        }
        val openableLayout = openableLayoutWeakReference?.get()
        if (openableLayoutWeakReference != null && openableLayout == null) {
            controller.removeOnDestinationChangedListener(this)
            return
        }
        val label = destination.label
        if (label != null) {
            // Fill in the data pattern with the args to build a valid URI
            val title = StringBuffer()
            val fillInPattern = Pattern.compile("\\{(.+?)\\}")
            val matcher = fillInPattern.matcher(label)
            while (matcher.find()) {
                val argName = matcher.group(1)
                if (arguments != null && arguments.containsKey(argName)) {
                    matcher.appendReplacement(title, "")
                    title.append(arguments[argName].toString())
                } else {
                    throw IllegalArgumentException(
                        "Could not find \"$argName\" in $arguments to fill label \"$label\""
                    )
                }
            }
            matcher.appendTail(title)
            setTitle(title)
        }
        val isTopLevelDestination = destination.matchDestinations(topLevelDestinations)
        if (openableLayout == null && isTopLevelDestination) {
            setNavigationIcon(null, 0)
        } else {
            setActionBarUpIndicator(openableLayout != null && isTopLevelDestination)
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun setActionBarUpIndicator(showAsDrawerIndicator: Boolean) {
        val (arrow, animate) = arrowDrawable?.run {
            this to true
        } ?: (DrawerArrowDrawable(context).also { arrowDrawable = it } to false)

        // random description used
        setNavigationIcon(
            arrow,
            if (showAsDrawerIndicator) R.string.all
            else R.string.all
        )

        val endValue = if (showAsDrawerIndicator) 0f else 1f
        if (animate) {
            val startValue = arrow.progress
            animator?.cancel()
            animator = ObjectAnimator.ofFloat(arrow, "progress", startValue, endValue)
            (animator as ObjectAnimator).start()
        } else {
            arrow.progress = endValue
        }
    }
}

fun NavDestination.matchDestinations(destinationIds: Set<Int?>): Boolean =
    hierarchy.any { destinationIds.contains(it.id) }


class ActionBarOnDestinationChangedListener(
    private val activity: AppCompatActivity,
    configuration: AppBarConfiguration
) : AbstractAppBarOnDestinationChangedListener(
    checkNotNull(activity.drawerToggleDelegate) {
        "Activity $activity does not have an DrawerToggleDelegate set"
    }.actionBarThemedContext,
    configuration
) {
    override fun setTitle(title: CharSequence?) {
        val actionBar = checkNotNull(activity.supportActionBar) {
            "Activity $activity does not have an ActionBar set via setSupportActionBar()"
        }
        actionBar.title = title
    }

    override fun setNavigationIcon(icon: Drawable?, @StringRes contentDescription: Int) {
        val actionBar = checkNotNull(activity.supportActionBar) {
            "Activity $activity does not have an ActionBar set via setSupportActionBar()"
        }
        actionBar.setDisplayHomeAsUpEnabled(icon != null)
        val delegate = checkNotNull(activity.drawerToggleDelegate) {
            "Activity $activity does not have an DrawerToggleDelegate set"
        }
        delegate.setActionBarUpIndicator(icon, contentDescription)
    }
}
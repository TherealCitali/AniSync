package com.anisync.android.presentation.components.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anisync.android.data.NavBarStyle

/**
 * Custom compact capsule bottom navigation bar.
 * Supports:
 *  - Compact floating capsule pill container (port from LastWave / modern capsule bar)
 *  - Active item expansion containing Icon + Label inside the capsule pill
 *  - Slot for Floating Action Button (More tabs FAB port from ArchiveTune)
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CompactNavBar(
    style: NavBarStyle,
    modifier: Modifier = Modifier,
    cornerRadius: Float = 32f,
    fabContent: (@Composable () -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    val systemBarInsets = WindowInsets.navigationBars.asPaddingValues()
    val radiusDp = cornerRadius.dp

    when (style) {
        NavBarStyle.FLOATING -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = systemBarInsets.calculateBottomPadding() + 8.dp,
                        top = 4.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(32.dp),
                        tonalElevation = 4.dp,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            content = content
                        )
                    }
                    if (fabContent != null) {
                        fabContent()
                    }
                }
            }
        }
        NavBarStyle.ANCHORED -> {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(topStart = radiusDp, topEnd = radiusDp),
                tonalElevation = 0.dp,
                modifier = modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 8.dp,
                            bottom = systemBarInsets.calculateBottomPadding() + 12.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    content = content
                )
            }
        }
    }
}

/**
 * A single item inside a [CompactNavBar].
 *
 * When selected, an active indicator pill surrounds the item containing both
 * the icon AND label side-by-side inside the pill (capsule style).
 */
@Composable
fun RowScope.CompactNavBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    showLabel: Boolean = true,
    modifier: Modifier = Modifier,
    badge: (@Composable () -> Unit)? = null
) {
    val indicatorColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            Color.Transparent
        },
        label = "nav-indicator-bg"
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "nav-icon-tint"
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "nav-label-color"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        indication = null,
        color = indicatorColor,
        shape = RoundedCornerShape(percent = 50),
        modifier = modifier.height(44.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (selected) 16.dp else 12.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompositionLocalProvider(LocalContentColor provides iconTint) {
                Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                    if (badge != null) badge() else icon()
                }
            }
            AnimatedVisibility(
                visible = selected && showLabel,
                enter = expandHorizontally(expandFrom = Alignment.Start) + fadeIn(),
                exit = shrinkHorizontally(shrinkTowards = Alignment.Start) + fadeOut()
            ) {
                CompositionLocalProvider(LocalContentColor provides labelColor) {
                    label()
                }
            }
        }
    }
}

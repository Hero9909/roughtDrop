package de.nebdir.roughtdrop

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import java.awt.dnd.*

class RoughtDropTarget : DropTarget() {

    val listeners = mutableListOf<(DropTargetDropEvent) -> Boolean>()

    override fun drop(dtde: DropTargetDropEvent?) {
        if (dtde != null) {
            if (listeners.firstOrNull {
                    it.invoke(dtde)
                } != null) return
        }
        super.drop(dtde)
    }
}

/**
 *
 */
fun Modifier.acceptDrop(
    window: ComposeWindow,
    enabled: Boolean,
    onDrop: (DropTargetDropEvent) -> Boolean
): Modifier = composed {
    Modifier.acceptDrop(
        window = window,
        onDrop = onDrop,
        enabled = enabled,
        indication = LocalIndication.current,
        mutableInteractionSource = remember { MutableInteractionSource() }
    )
}

fun Modifier.acceptDrop(
    window: ComposeWindow,
    enabled: Boolean,
    onDrop: (DropTargetDropEvent) -> Boolean,
    indication: Indication,
    mutableInteractionSource: MutableInteractionSource
): Modifier = composed(
    factory = {

        var size by remember { mutableStateOf<LayoutCoordinates?>(null) }

        val listener = { it: DropTargetDropEvent ->
            val localSize = size
            if (localSize != null) {
                val localOffset = localSize.windowToLocal(
                    Offset(it.location.x.toFloat(), it.location.y.toFloat())
                )
                if (localOffset.isValid() && localOffset.x > 0 && localOffset.x < localSize.size.width && localOffset.y > 0 && localOffset.y < localSize.size.height) {
                    onDrop(it)
                } else
                    false
            } else
                false
        }

        DisposableEffect(Unit) {
            (window.contentPane.dropTarget as RoughtDropTarget).listeners.add(listener)
            onDispose {
                (window.contentPane.dropTarget as RoughtDropTarget).listeners.remove(listener)
            }
        }

        Modifier
            .indication(mutableInteractionSource, indication)
            .hoverable(enabled = enabled, interactionSource = mutableInteractionSource)
            .onGloballyPositioned { size = it }
    }
)
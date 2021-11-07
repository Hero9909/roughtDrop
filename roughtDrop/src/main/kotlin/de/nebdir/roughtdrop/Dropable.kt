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
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.awt.dnd.DropTargetListener

/**
 *
 */
fun Modifier.acceptDrop(
    window: ComposeWindow,
    enabled: Boolean,
    onDrop: (DropTargetDropEvent) -> Unit
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
    onDrop: (DropTargetDropEvent) -> Unit,
    indication: Indication,
    mutableInteractionSource: MutableInteractionSource
): Modifier = composed(
    factory = {

        var size by remember { mutableStateOf<LayoutCoordinates?>(null) }

        val listener = object : DropTargetListener {
            override fun dragEnter(dtde: DropTargetDragEvent?) {}
            override fun dragOver(dtde: DropTargetDragEvent?) {}
            override fun dropActionChanged(dtde: DropTargetDragEvent?) {}
            override fun dragExit(dte: DropTargetEvent?) {}

            override fun drop(dtde: DropTargetDropEvent?) {
                val localSize = size
                if (dtde != null && localSize != null) {
                    val localOffset = localSize.windowToLocal(
                        Offset(dtde.location.x.toFloat(), dtde.location.y.toFloat())
                    )
                    if (localOffset.isValid() && localOffset.x>0 && localOffset.x < localSize.size.width && localOffset.y > 0 && localOffset.y < localSize.size.height)
                        onDrop(dtde)
                }
            }

        }

        DisposableEffect(Unit) {
            window.contentPane.dropTarget?.addDropTargetListener(listener)
            onDispose {
                window.contentPane.dropTarget?.removeDropTargetListener(listener)
            }
        }

        Modifier
            .indication(mutableInteractionSource, indication)
            .hoverable(enabled = enabled, interactionSource = mutableInteractionSource)
            .onGloballyPositioned { size = it }
    }
)
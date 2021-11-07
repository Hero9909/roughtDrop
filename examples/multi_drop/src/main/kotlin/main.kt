import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import de.nebdir.roughtdrop.RoughtDropTarget
import de.nebdir.roughtdrop.acceptDrop
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.io.File

fun main() {
    singleWindowApplication {
        window.contentPane.dropTarget = RoughtDropTarget()
        MaterialTheme {
            Surface {
                Column (
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var items by remember {
                        mutableStateOf(2)
                    }
                    Button({items++}){
                        Text("add Item")
                    }
                    LazyColumn {
                        items(items){
                            DropBox(window)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropBox(window:ComposeWindow) {
    var file by remember {
        mutableStateOf<File?>(null)
    }
    Box(
        Modifier.fillMaxWidth(0.4f).padding(8.dp)
            .border(BorderStroke(2.dp, Color.LightGray), shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .acceptDrop(window, true) {
                it.acceptDrop(DnDConstants.ACTION_REFERENCE)
                val files =
                    it.transferable.getTransferData(DataFlavor.javaFileListFlavor) as? List<File>
                        ?: emptyList<File>()
                file = files.firstOrNull()
                true
            }.clickable(enabled = file != null) {
                file = null
            }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (file == null)
            Text("Drop File here")
        else {
            Column {
                Text(file!!.absolutePath)
                Text("click to clear")
            }
        }
    }
}


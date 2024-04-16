package agh.ryszard.blazej.heartbeat_app.ui.elements

import agh.ryszard.blazej.heartbeat_app.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CornerDecoration(modifier: Modifier = Modifier){
    Image(
        painter = painterResource(R.drawable.corner_decoration),
        contentDescription = "Corner decorator",
        contentScale = ContentScale.FillBounds,
        modifier = modifier
            .width(300.dp)
            .height(150.dp)
    )
}

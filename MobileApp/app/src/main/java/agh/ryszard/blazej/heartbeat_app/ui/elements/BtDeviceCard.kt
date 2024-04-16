package agh.ryszard.blazej.heartbeat_app.ui.elements

import agh.ryszard.blazej.heartbeat_app.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun BtDeviceCard(icon: Painter, name: String, extraInfo: String, onClick: () -> Unit){
    OutlinedCard (
        colors = CardDefaults.cardColors(
            containerColor = if(extraInfo != "") MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Icon(
                icon,
                contentDescription = stringResource(R.string.heart_icon_content_desc),
                modifier = Modifier
                    .requiredSize(32.dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
            )
            {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(bottom = 2.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if(extraInfo != "") {
                    Text(
                        text = extraInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun BtDeviceCard(icon: ImageVector, name: String, extraInfo: String, onClick: () -> Unit){
    OutlinedCard (
        colors = CardDefaults.cardColors(
            containerColor = if(extraInfo != "") MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Icon(
                icon,
                contentDescription = stringResource(R.string.heart_icon_content_desc),
                modifier = Modifier
                    .requiredSize(32.dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
            )
            {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(bottom = 2.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if(extraInfo != "") {
                    Text(
                        text = extraInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
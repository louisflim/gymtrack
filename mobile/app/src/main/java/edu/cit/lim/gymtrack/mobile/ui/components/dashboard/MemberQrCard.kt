package edu.cit.lim.gymtrack.mobile.ui.components.dashboard

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBorder
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextMuted

@Composable
fun MemberQrCard(
    qrImageBase64: String?,
    loading: Boolean,
    note: String = "Present this QR code to staff for attendance check-in/check-out."
) {
    DashboardSectionCard(title = "My Member QR") {
        if (loading) {
            CircularProgressIndicator(color = GymTrackAccent)
        } else {
            val imageBitmap = qrImageBase64?.let { base64 ->
                runCatching {
                    val bytes = Base64.decode(base64, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
                }.getOrNull()
            }

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "My QR Code",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .border(1.dp, GymTrackBorder)
                        .padding(6.dp)
                )
            } else {
                Text(
                    text = "Unable to load QR image.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GymTrackTextMuted
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = note,
            style = MaterialTheme.typography.bodyMedium,
            color = GymTrackTextMuted
        )
    }
}

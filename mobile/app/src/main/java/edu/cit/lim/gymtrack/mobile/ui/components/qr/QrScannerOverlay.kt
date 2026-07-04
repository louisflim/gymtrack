package edu.cit.lim.gymtrack.mobile.ui.components.qr

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBackground
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBorder
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextMuted

@Composable
fun QrScannerOverlay(
    onScan: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val barcodeViewRef = remember { mutableStateOf<DecoratedBarcodeView?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) {
            onClose()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val releaseCamera = {
        barcodeViewRef.value?.pause()
        barcodeViewRef.value = null
    }

    val closeScanner = {
        releaseCamera()
        onClose()
    }

    DisposableEffect(Unit) {
        onDispose {
            releaseCamera()
        }
    }

    Dialog(
        onDismissRequest = closeScanner,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (hasPermission) {
                AndroidView(
                    factory = { ctx ->
                        DecoratedBarcodeView(ctx).apply {
                            viewFinder.setLaserVisibility(false)
                            decodeContinuous(object : BarcodeCallback {
                                override fun barcodeResult(result: BarcodeResult?) {
                                    val text = result?.text ?: return
                                    pause()
                                    onScan(text)
                                }
                            })
                            barcodeViewRef.value = this
                            resume()
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    onRelease = { view ->
                        view.pause()
                        if (barcodeViewRef.value === view) {
                            barcodeViewRef.value = null
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color(0xE6000000))
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Scan GymTrack QR",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Button(
                        onClick = closeScanner,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GymTrackBackground,
                            contentColor = GymTrackAccent
                        ),
                        border = BorderStroke(1.dp, GymTrackBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close scanner",
                            tint = GymTrackAccent
                        )
                        Text(
                            text = "Close",
                            modifier = Modifier.padding(start = 6.dp),
                            color = GymTrackAccent
                        )
                    }
                }
            }

            Text(
                text = if (hasPermission) {
                    "Align the QR code inside the camera view."
                } else {
                    "Camera permission is required to scan QR codes."
                },
                color = GymTrackTextMuted,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(24.dp)
            )
        }
    }
}

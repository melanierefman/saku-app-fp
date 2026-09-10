package com.example.saku.app.core.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.FlipHorizontal
import com.composables.icons.lucide.Image
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Sparkles
import com.composables.icons.lucide.SwitchCamera
import com.composables.icons.lucide.UserCheck
import com.composables.icons.lucide.X
import com.composables.icons.lucide.Zap
import com.composables.icons.lucide.ZapOff
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

enum class CameraCaptureMode {
    KTP,
    SELFIE
}

/**
 * In-App Guided Camera Dialog dengan Frame Cutout & Posisi Framing Khusus e-KTP / Wajah Selfie
 */
@Composable
fun CameraFramingCaptureDialog(
    mode: CameraCaptureMode = CameraCaptureMode.KTP,
    onDismissRequest: () -> Unit,
    onImageCaptured: (Bitmap) -> Unit,
    onPickGalleryRequested: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            onDismissRequest()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            CameraFramingContent(
                mode = mode,
                onDismiss = onDismissRequest,
                onImageCaptured = onImageCaptured,
                onPickGalleryRequested = onPickGalleryRequested
            )
        }
    }
}

@Composable
private fun CameraFramingContent(
    mode: CameraCaptureMode,
    onDismiss: () -> Unit,
    onImageCaptured: (Bitmap) -> Unit,
    onPickGalleryRequested: (() -> Unit)?
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val density = LocalDensity.current

    var lensFacing by remember {
        mutableIntStateOf(
            if (mode == CameraCaptureMode.SELFIE) CameraSelector.LENS_FACING_FRONT
            else CameraSelector.LENS_FACING_BACK
        )
    }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var isCapturing by remember { mutableStateOf(false) }

    val previewView = remember { PreviewView(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    // Bind CameraX Lifecycle
    LaunchedEffect(lensFacing, flashMode) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val capture = ImageCapture.Builder()
            .setFlashMode(flashMode)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
        imageCapture = capture

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                capture
            )
        } catch (e: Exception) {
            Log.e("CameraFraming", "Gagal bind camera lifecycle", e)
        }
    }

    // Scanning animated line effect
    val infiniteTransition = rememberInfiniteTransition(label = "scan_transition")
    val scanAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_line"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val screenWidthPx = with(density) { screenWidth.toPx() }
        val screenHeightPx = with(density) { screenHeight.toPx() }

        // Camera Live Preview
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Calculate Framing Cutout
        // KTP aspect ratio is 85.6mm : 53.98mm ~ 1.585
        val frameWidthPx: Float
        val frameHeightPx: Float
        val frameRect: Rect

        if (mode == CameraCaptureMode.KTP) {
            frameWidthPx = screenWidthPx * 0.88f
            frameHeightPx = frameWidthPx / 1.585f
            val left = (screenWidthPx - frameWidthPx) / 2f
            val top = (screenHeightPx - frameHeightPx) / 2.3f // slightly above center to leave room for bottom controls
            frameRect = Rect(left, top, left + frameWidthPx, top + frameHeightPx)
        } else {
            // Selfie Oval
            frameWidthPx = screenWidthPx * 0.72f
            frameHeightPx = frameWidthPx * 1.35f
            val left = (screenWidthPx - frameWidthPx) / 2f
            val top = (screenHeightPx - frameHeightPx) / 2.3f
            frameRect = Rect(left, top, left + frameWidthPx, top + frameHeightPx)
        }

        // Overlay with Clear Cutout Hole & Guideline Borders
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.99f } // Required for BlendMode.Clear to work in Compose
        ) {
            // 1. Semi-transparent black dark backdrop
            drawRect(color = Color(0xCC000000))

            // 2. Cutout Hole
            if (mode == CameraCaptureMode.KTP) {
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = frameRect.topLeft,
                    size = frameRect.size,
                    cornerRadius = CornerRadius(24f, 24f),
                    blendMode = BlendMode.Clear
                )

                // Outer border line
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.4f),
                    topLeft = frameRect.topLeft,
                    size = frameRect.size,
                    cornerRadius = CornerRadius(24f, 24f),
                    style = Stroke(width = 2f)
                )

                // Glowing Orange Corner Markers
                val cornerLength = 48f
                val strokeWidth = 8f
                val cornerRadius = 24f

                // Top Left
                drawPath(
                    path = Path().apply {
                        moveTo(frameRect.left, frameRect.top + cornerLength)
                        lineTo(frameRect.left, frameRect.top + cornerRadius)
                        quadraticTo(frameRect.left, frameRect.top, frameRect.left + cornerRadius, frameRect.top)
                        lineTo(frameRect.left + cornerLength, frameRect.top)
                    },
                    color = Primary,
                    style = Stroke(width = strokeWidth)
                )

                // Top Right
                drawPath(
                    path = Path().apply {
                        moveTo(frameRect.right - cornerLength, frameRect.top)
                        lineTo(frameRect.right - cornerRadius, frameRect.top)
                        quadraticTo(frameRect.right, frameRect.top, frameRect.right, frameRect.top + cornerRadius)
                        lineTo(frameRect.right, frameRect.top + cornerLength)
                    },
                    color = Primary,
                    style = Stroke(width = strokeWidth)
                )

                // Bottom Left
                drawPath(
                    path = Path().apply {
                        moveTo(frameRect.left, frameRect.bottom - cornerLength)
                        lineTo(frameRect.left, frameRect.bottom - cornerRadius)
                        quadraticTo(frameRect.left, frameRect.bottom, frameRect.left + cornerRadius, frameRect.bottom)
                        lineTo(frameRect.left + cornerLength, frameRect.bottom)
                    },
                    color = Primary,
                    style = Stroke(width = strokeWidth)
                )

                // Bottom Right
                drawPath(
                    path = Path().apply {
                        moveTo(frameRect.right - cornerLength, frameRect.bottom)
                        lineTo(frameRect.right - cornerRadius, frameRect.bottom)
                        quadraticTo(frameRect.right, frameRect.bottom, frameRect.right, frameRect.bottom - cornerRadius)
                        lineTo(frameRect.right, frameRect.bottom - cornerLength)
                    },
                    color = Primary,
                    style = Stroke(width = strokeWidth)
                )

                // Animated Scan Line
                val scanY = frameRect.top + (frameRect.height * scanAnim)
                drawLine(
                    color = Primary.copy(alpha = 0.65f),
                    start = Offset(frameRect.left + 24f, scanY),
                    end = Offset(frameRect.right - 24f, scanY),
                    strokeWidth = 3f
                )

            } else {
                // Selfie Oval Cutout
                drawOval(
                    color = Color.Transparent,
                    topLeft = frameRect.topLeft,
                    size = frameRect.size,
                    blendMode = BlendMode.Clear
                )

                drawOval(
                    color = Primary,
                    topLeft = frameRect.topLeft,
                    size = frameRect.size,
                    style = Stroke(width = 6f)
                )
            }
        }

        // Top Header Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Tutup",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Mode Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x99000000),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (mode == CameraCaptureMode.KTP) Lucide.CreditCard else Lucide.UserCheck,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (mode == CameraCaptureMode.KTP) "Pindai e-KTP" else "Verifikasi Wajah",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Flash Mode Toggle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                        .clickable {
                            flashMode = when (flashMode) {
                                ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                                ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                                else -> ImageCapture.FLASH_MODE_OFF
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (flashMode == ImageCapture.FLASH_MODE_ON) Lucide.Zap else Lucide.ZapOff,
                        contentDescription = "Flash",
                        tint = if (flashMode == ImageCapture.FLASH_MODE_ON) Primary else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Bottom Overlay Guidelines & Shutter Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Instructions Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xCC1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.Sparkles,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (mode == CameraCaptureMode.KTP)
                            "Posisikan e-KTP pas di dalam bingkai oranye. Pastikan teks terbaca jelas dan bebas pantulan cahaya."
                        else
                            "Posisikan wajah Anda di dalam lingkaran oval dan pastikan pencahayaan cukup.",
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Controls Bar (Gallery, Shutter Button, Switch Camera)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Picker Button
                if (onPickGalleryRequested != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                onDismiss()
                                onPickGalleryRequested()
                            }
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0x66FFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Image,
                                contentDescription = "Galeri",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Galeri",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(56.dp))
                }

                // Shutter Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.35f))
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(enabled = !isCapturing && imageCapture != null) {
                            val capture = imageCapture ?: return@clickable
                            isCapturing = true

                            val executor = ContextCompat.getMainExecutor(context)
                            capture.takePicture(
                                executor,
                                object : ImageCapture.OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                        try {
                                            val rawBitmap = imageProxyToBitmap(imageProxy)
                                            imageProxy.close()

                                            // Crop bitmap directly to the framed viewport area for maximum OCR clarity
                                            val croppedBitmap = cropBitmapToFrame(
                                                rawBitmap = rawBitmap,
                                                screenWidth = screenWidthPx,
                                                screenHeight = screenHeightPx,
                                                frameRect = frameRect
                                            )

                                            onImageCaptured(croppedBitmap)
                                            onDismiss()
                                        } catch (e: Exception) {
                                            Log.e("CameraFraming", "Error processing captured frame", e)
                                        } finally {
                                            isCapturing = false
                                        }
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Log.e("CameraFraming", "Capture error", exception)
                                        isCapturing = false
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            color = Primary,
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Primary)
                        )
                    }
                }

                // Camera Switch Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                                CameraSelector.LENS_FACING_FRONT
                            else
                                CameraSelector.LENS_FACING_BACK
                        }
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0x66FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.SwitchCamera,
                            contentDescription = "Ganti Kamera",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Putar",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Mengonversi ImageProxy dari CameraX menjadi Bitmap dengan rotasi yang benar
 */
private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
    val buffer = imageProxy.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    val rotation = imageProxy.imageInfo.rotationDegrees
    return if (rotation != 0) {
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated != bitmap) {
            bitmap.recycle()
        }
        rotated
    } else {
        bitmap
    }
}

/**
 * Memotong (Crop) bitmap tepat pada area pandang frame guide e-KTP / Oval wajah
 * dengan sedikit margin 6% agar tidak ada sudut atau teks pinggir yang terpotong.
 */
private fun cropBitmapToFrame(
    rawBitmap: Bitmap,
    screenWidth: Float,
    screenHeight: Float,
    frameRect: Rect
): Bitmap {
    val bmpWidth = rawBitmap.width.toFloat()
    val bmpHeight = rawBitmap.height.toFloat()

    // Hitung rasio skala tampilan terhadap ukuran bitmap sesungguhnya
    val scale = max(bmpWidth / screenWidth, bmpHeight / screenHeight)

    // Posisi relatif di dalam bitmap sesungguhnya
    val displayedBmpWidth = screenWidth * scale
    val displayedBmpHeight = screenHeight * scale
    val offsetX = (displayedBmpWidth - bmpWidth) / 2f
    val offsetY = (displayedBmpHeight - bmpHeight) / 2f

    // Tambahkan sedikit safety margin (6%) agar teks tidak terpotong
    val marginX = (frameRect.width * 0.06f)
    val marginY = (frameRect.height * 0.06f)

    val cropLeft = ((frameRect.left - marginX) * scale - offsetX).toInt().coerceIn(0, rawBitmap.width - 1)
    val cropTop = ((frameRect.top - marginY) * scale - offsetY).toInt().coerceIn(0, rawBitmap.height - 1)
    val cropRight = ((frameRect.right + marginX) * scale - offsetX).toInt().coerceIn(cropLeft + 1, rawBitmap.width)
    val cropBottom = ((frameRect.bottom + marginY) * scale - offsetY).toInt().coerceIn(cropTop + 1, rawBitmap.height)

    val cropWidth = cropRight - cropLeft
    val cropHeight = cropBottom - cropTop

    return if (cropWidth > 50 && cropHeight > 50) {
        val cropped = Bitmap.createBitmap(rawBitmap, cropLeft, cropTop, cropWidth, cropHeight)
        if (cropped != rawBitmap) {
            rawBitmap.recycle()
        }
        cropped
    } else {
        rawBitmap
    }
}

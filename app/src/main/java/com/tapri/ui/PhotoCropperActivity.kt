package com.tapri.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.tapri.R
import com.tapri.utils.AnimationUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PhotoCropperActivity : AppCompatActivity() {
    
    private lateinit var backButton: TextView
    private lateinit var cropButton: TextView
    private lateinit var cancelButton: TextView
    private lateinit var imageView: ImageView
    private lateinit var cropFrame: View
    
    private var inputImageUri: Uri? = null
    private var outputImagePath: String? = null
    private var originalBitmap: Bitmap? = null
    private var cropX = 0f
    private var cropY = 0f
    private var cropWidth = 280f
    private var cropHeight = 350f
    
    companion object {
        const val EXTRA_IMAGE_URI = "image_uri"
        const val RESULT_CROPPED_IMAGE = "cropped_image_path"
        const val RESULT_IMAGE_URI = "cropped_image_uri"
        
        // Tapri post dimensions (16:9 aspect ratio for better social media display)
        const val TAPRI_POST_WIDTH = 1080
        const val TAPRI_POST_HEIGHT = 1350 // 4:5 aspect ratio for Instagram-like posts
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_cropper)
        
        inputImageUri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_IMAGE_URI, android.net.Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_IMAGE_URI)
        }
        
        if (inputImageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        initializeViews()
        setupClickListeners()
        loadImage()
    }
    
    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        cropButton = findViewById(R.id.cropButton)
        cancelButton = findViewById(R.id.cancelButton)
        imageView = findViewById(R.id.imageView)
        cropFrame = findViewById(R.id.cropFrame)
    }
    
    private fun setupClickListeners() {
        backButton.setOnClickListener {
            AnimationUtils.animateButtonPress(backButton) {
                finish()
            }
        }
        
        cancelButton.setOnClickListener {
            AnimationUtils.animateButtonPress(cancelButton) {
                finish()
            }
        }
        
        cropButton.setOnClickListener {
            AnimationUtils.animateButtonPress(cropButton) {
                performCrop()
            }
        }
        
        // Set up touch handling for crop frame
        setupCropFrameTouch()
    }
    
    private fun loadImage() {
        try {
            // Load the image using Glide
            Glide.with(this)
                .load(inputImageUri)
                .into(imageView)
                
            // Load bitmap for cropping
            originalBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(inputImageUri!!))
            
        } catch (e: Exception) {
            Log.e("PhotoCropper", "Error loading image", e)
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun setupCropFrameTouch() {
        var initialX = 0f
        var initialY = 0f
        var initialLeft = 0
        var initialTop = 0
        
        cropFrame.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Get initial touch position and frame position
                    initialX = event.rawX
                    initialY = event.rawY
                    val layoutParams = view.layoutParams as FrameLayout.LayoutParams
                    initialLeft = layoutParams.leftMargin
                    initialTop = layoutParams.topMargin
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    // Calculate movement delta
                    val deltaX = event.rawX - initialX
                    val deltaY = event.rawY - initialY
                    
                    // Update crop frame position
                    val layoutParams = view.layoutParams as FrameLayout.LayoutParams
                    val newLeft = (initialLeft + deltaX).toInt()
                    val newTop = (initialTop + deltaY).toInt()
                    
                    // Constrain to image bounds
                    val maxLeft = imageView.width - view.width
                    val maxTop = imageView.height - view.height
                    
                    layoutParams.leftMargin = newLeft.coerceIn(0, maxLeft)
                    layoutParams.topMargin = newTop.coerceIn(0, maxTop)
                    view.layoutParams = layoutParams
                    
                    // Update crop coordinates
                    cropX = layoutParams.leftMargin.toFloat()
                    cropY = layoutParams.topMargin.toFloat()
                    
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // End dragging
                    true
                }
                else -> false
            }
        }
    }
    
    private fun performCrop() {
        if (originalBitmap == null) {
            Toast.makeText(this, "No image loaded", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            // Create output directory
            val outputDir = File(cacheDir, "cropped_images")
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }
            
            // Create output file
            val outputFile = File(outputDir, "cropped_${System.currentTimeMillis()}.jpg")
            
            // Perform manual crop based on crop frame position
            val croppedBitmap = cropBitmap(originalBitmap!!)
            
            // Save cropped bitmap
            val outputStream = FileOutputStream(outputFile)
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
            
            outputImagePath = outputFile.absolutePath
            
            val resultIntent = Intent()
            resultIntent.putExtra(RESULT_IMAGE_URI, Uri.fromFile(outputFile))
            resultIntent.putExtra(RESULT_CROPPED_IMAGE, outputFile.absolutePath)
            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, "Photo cropped successfully!", Toast.LENGTH_SHORT).show()
            finish()
            
        } catch (e: Exception) {
            Log.e("PhotoCropper", "Error cropping image", e)
            Toast.makeText(this, "Error cropping image", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun cropBitmap(bitmap: Bitmap): Bitmap {
        // Get the current crop frame position
        val layoutParams = cropFrame.layoutParams as FrameLayout.LayoutParams
        val currentCropX = layoutParams.leftMargin.toFloat()
        val currentCropY = layoutParams.topMargin.toFloat()
        val currentCropWidth = cropFrame.width.toFloat()
        val currentCropHeight = cropFrame.height.toFloat()
        
        // Calculate how the image is scaled in the ImageView
        val imageViewWidth = imageView.width.toFloat()
        val imageViewHeight = imageView.height.toFloat()
        val bitmapWidth = bitmap.width.toFloat()
        val bitmapHeight = bitmap.height.toFloat()
        
        // Calculate the actual displayed image dimensions within the ImageView
        // ImageView uses centerInside scale type, so we need to account for aspect ratio
        val imageViewAspectRatio = imageViewWidth / imageViewHeight
        val bitmapAspectRatio = bitmapWidth / bitmapHeight
        
        val displayedImageWidth: Float
        val displayedImageHeight: Float
        val imageOffsetX: Float
        val imageOffsetY: Float
        
        if (bitmapAspectRatio > imageViewAspectRatio) {
            // Image is wider than ImageView, so height fits and width is cropped
            displayedImageHeight = imageViewHeight
            displayedImageWidth = imageViewHeight * bitmapAspectRatio
            imageOffsetX = (imageViewWidth - displayedImageWidth) / 2f
            imageOffsetY = 0f
        } else {
            // Image is taller than ImageView, so width fits and height is cropped
            displayedImageWidth = imageViewWidth
            displayedImageHeight = imageViewWidth / bitmapAspectRatio
            imageOffsetX = 0f
            imageOffsetY = (imageViewHeight - displayedImageHeight) / 2f
        }
        
        // Calculate scale factors from displayed image to actual bitmap
        val scaleX = bitmapWidth / displayedImageWidth
        val scaleY = bitmapHeight / displayedImageHeight
        
        // Convert crop frame coordinates to bitmap coordinates
        // First, adjust crop coordinates relative to the displayed image
        val adjustedCropX = currentCropX - imageOffsetX
        val adjustedCropY = currentCropY - imageOffsetY
        
        // Then convert to bitmap coordinates
        val cropLeft = (adjustedCropX * scaleX).toInt().coerceAtLeast(0)
        val cropTop = (adjustedCropY * scaleY).toInt().coerceAtLeast(0)
        val cropRight = ((adjustedCropX + currentCropWidth) * scaleX).toInt().coerceAtMost(bitmap.width)
        val cropBottom = ((adjustedCropY + currentCropHeight) * scaleY).toInt().coerceAtMost(bitmap.height)
        
        // Ensure valid crop dimensions
        val cropW = cropRight - cropLeft
        val cropH = cropBottom - cropTop
        
        Log.d("PhotoCropper", "Crop frame: x=$currentCropX, y=$currentCropY, w=$currentCropWidth, h=$currentCropHeight")
        Log.d("PhotoCropper", "Image view: w=$imageViewWidth, h=$imageViewHeight")
        Log.d("PhotoCropper", "Bitmap: w=$bitmapWidth, h=$bitmapHeight")
        Log.d("PhotoCropper", "Displayed image: w=$displayedImageWidth, h=$displayedImageHeight")
        Log.d("PhotoCropper", "Image offset: x=$imageOffsetX, y=$imageOffsetY")
        Log.d("PhotoCropper", "Adjusted crop: x=$adjustedCropX, y=$adjustedCropY")
        Log.d("PhotoCropper", "Scale factors: scaleX=$scaleX, scaleY=$scaleY")
        Log.d("PhotoCropper", "Crop area: left=$cropLeft, top=$cropTop, right=$cropRight, bottom=$cropBottom")
        
        if (cropW <= 0 || cropH <= 0) {
            Log.w("PhotoCropper", "Invalid crop dimensions, using center crop")
            // Fallback to center crop
            val size = minOf(bitmap.width, bitmap.height)
            val x = (bitmap.width - size) / 2
            val y = (bitmap.height - size) / 2
            return Bitmap.createBitmap(bitmap, x, y, size, size)
        }
        
        return Bitmap.createBitmap(bitmap, cropLeft, cropTop, cropW, cropH)
    }
}

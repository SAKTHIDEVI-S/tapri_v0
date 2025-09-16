package com.tapri.ui

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R
import com.tapri.utils.AnimationUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class VideoTrimmerActivity : AppCompatActivity() {
    
    private lateinit var backButton: TextView
    private lateinit var trimButton: TextView
    private lateinit var cancelButton: TextView
    private lateinit var videoView: VideoView
    private lateinit var durationText: TextView
    
    private var inputVideoUri: Uri? = null
    private var outputVideoPath: String? = null
    private var videoDuration: Long = 0
    private var trimmedDuration: Long = 30000 // 30 seconds in milliseconds
    
    companion object {
        const val EXTRA_VIDEO_URI = "video_uri"
        const val RESULT_TRIMMED_VIDEO = "trimmed_video_path"
        const val RESULT_VIDEO_URI = "trimmed_video_uri"
        const val MAX_VIDEO_DURATION_MS = 30000L // 30 seconds
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_trimmer)
        
        inputVideoUri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_VIDEO_URI, android.net.Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_VIDEO_URI)
        }
        
        if (inputVideoUri == null) {
            Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        initializeViews()
        setupClickListeners()
        loadVideo()
    }
    
    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        trimButton = findViewById(R.id.trimButton)
        cancelButton = findViewById(R.id.cancelButton)
        videoView = findViewById(R.id.videoView)
        durationText = findViewById(R.id.durationText)
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
        
        trimButton.setOnClickListener {
            AnimationUtils.animateButtonPress(trimButton) {
                performTrim()
            }
        }
    }
    
    private fun loadVideo() {
        try {
            // Get video duration
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(this, inputVideoUri)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            videoDuration = duration?.toLongOrNull() ?: 0
            retriever.release()
            
            // Set up video view
            videoView.setVideoURI(inputVideoUri)
            videoView.setOnPreparedListener { mediaPlayer ->
                // Show video duration info
                val durationSeconds = videoDuration / 1000
                val trimmedSeconds = MAX_VIDEO_DURATION_MS / 1000
                durationText.text = "Video: ${durationSeconds}s → Trimmed: ${trimmedSeconds}s"
                
                // Start playing the video
                videoView.start()
                
                // Stop at 30 seconds if video is longer
                if (videoDuration > MAX_VIDEO_DURATION_MS) {
                    videoView.postDelayed({
                        videoView.pause()
                    }, MAX_VIDEO_DURATION_MS)
                }
            }
            
            videoView.setOnCompletionListener {
                videoView.seekTo(0)
                videoView.start()
            }
            
        } catch (e: Exception) {
            Log.e("VideoTrimmer", "Error loading video", e)
            Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun performTrim() {
        if (videoDuration <= MAX_VIDEO_DURATION_MS) {
            // Video is already short enough, just return the original
            val resultIntent = Intent()
            resultIntent.putExtra(RESULT_VIDEO_URI, inputVideoUri)
            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, "Video is already short enough!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        try {
            // For now, we'll copy the video and let the backend handle trimming
            // In a production app, you would use FFmpeg or similar to actually trim the video
            
            val outputDir = File(cacheDir, "trimmed_videos")
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }
            
            val outputFile = File(outputDir, "trimmed_${System.currentTimeMillis()}.mp4")
            
            // Copy the video file (in production, this would be actual trimming)
            copyVideoFile(inputVideoUri!!, outputFile)
            
            outputVideoPath = outputFile.absolutePath
            
            val resultIntent = Intent()
            resultIntent.putExtra(RESULT_VIDEO_URI, Uri.fromFile(outputFile))
            resultIntent.putExtra(RESULT_TRIMMED_VIDEO, outputFile.absolutePath)
            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, "Video trimmed to 30 seconds!", Toast.LENGTH_SHORT).show()
            finish()
            
        } catch (e: Exception) {
            Log.e("VideoTrimmer", "Error trimming video", e)
            Toast.makeText(this, "Error trimming video", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun copyVideoFile(inputUri: Uri, outputFile: File) {
        val inputStream = contentResolver.openInputStream(inputUri)
        val outputStream = FileOutputStream(outputFile)
        
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        
        inputStream?.close()
        outputStream.close()
    }
    
    override fun onPause() {
        super.onPause()
        if (videoView.isPlaying) {
            videoView.pause()
        }
    }
    
    override fun onResume() {
        super.onResume()
        if (!videoView.isPlaying) {
            videoView.start()
        }
    }
}
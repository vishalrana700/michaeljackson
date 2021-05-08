//package com.videosong.ui.video_play
//
//import android.net.Uri
//import android.os.Bundle
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import androidx.databinding.DataBindingUtil
//import androidx.media2.exoplayer.external.ExoPlayerFactory
//import androidx.media2.exoplayer.external.SimpleExoPlayer
//import com.app.moodmi.databinding.ActivityVideoDetailBinding
//import com.app.moodmi.patient_new.model.PatientMediaResponse
//import com.app.moodmi.utils.AppUtils
//import com.app.moodmi.utils.VideoPlayerConfig
//import com.google.android.exoplayer2.*
//import com.google.android.exoplayer2.source.ExtractorMediaSource
//import com.google.android.exoplayer2.source.MediaSource
//import com.google.android.exoplayer2.source.TrackGroupArray
//import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
//import com.google.android.exoplayer2.trackselection.TrackSelectionArray
//import com.google.android.exoplayer2.trackselection.TrackSelector
//import com.google.android.exoplayer2.upstream.DataSource
//import com.google.android.exoplayer2.upstream.DefaultAllocator
//import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import com.google.android.exoplayer2.util.Util
//import java.lang.Exception
//
///**
// * Created by Manjeet Verma on 25, November, 2020.
// */
//class MediaDetailActivity : AppCompatActivity(), Player.EventListener {
//
//    companion object {
//        const val VIDEO_DETAILS = "video_details"
//    }
//
//    private lateinit var binding: ActivityVideoDetailBinding
//    private lateinit var video: PatientMediaResponse.Video
//
//    // Video Player
//    var player: SimpleExoPlayer? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_detail)
//
//        if (intent.hasExtra(VIDEO_DETAILS)) {
//            // Video Details Object
//            val vid = intent.extras?.getSerializable(VIDEO_DETAILS) as? PatientMediaResponse.Video?
//            // If Video Object is Null Finish Activity
//            if(vid == null) {
//                finish()
//            }
//            // Assign to Video Object
//            video = vid as PatientMediaResponse.Video
//        }
//
//        // Set Base Data
//        init()
//        // Set On Clicks
//        setOnClickListeners()
//    }
//
//    private fun setOnClickListeners() {
//        binding.imgBack.setOnClickListener {
//            finish()
//        }
//    }
//
//    private fun init() {
//
//        // Set Video Meta Info
//        binding.txtTitle.text = video.title
//        binding.txtDescription.text = video.description
//
//        // Time for Media Create
//        binding.txtDate.text = AppUtils.getDateShortDayMonth(video.createdAt, true)
//
//        // is Premium or Not
//        if(video.isPremium) {
//            binding.txtPremium.visibility = View.VISIBLE
//        }
//
//        // Set Video Player
//        val loadControl: LoadControl = DefaultLoadControl(
//            DefaultAllocator(true, 16),
//            VideoPlayerConfig.MIN_BUFFER_DURATION,
//            VideoPlayerConfig.MAX_BUFFER_DURATION,
//            VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
//            VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true
//        )
//
//        //  Currently Available Bandwidth Estimate
//        val vtsFactory = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
//
//        // Track Selections
//        val trackSelector: TrackSelector = DefaultTrackSelector(vtsFactory)
//
//        // Create the player
//        player = ExoPlayerFactory.newSimpleInstance(
//            DefaultRenderersFactory(this), trackSelector, loadControl
//        )
//
//        binding.videoView.player = player
//
//        // Produces DataSource instances through which media data is loaded.
//        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
//            this, Util.getUserAgent(this, getString(R.string.app_name)), DefaultBandwidthMeter()
//        )
//
//        // Assign Default as Audio
//        var mediaUrl: String? = video.mediaAudio ?: ""
//
//        // If Empty Assign Video Media
//        if(mediaUrl.isNullOrEmpty()) {
//            mediaUrl = video.mediaVideo
//        }
//        try {
//            // This is the MediaSource representing the media to be played.
//            val videoSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(Uri.parse(mediaUrl))
//
//            // Prepare the player with the source.
//            player?.prepare(videoSource)
//            player?.playWhenReady = false
//            player?.addListener(this)
//        } catch (e: Exception) {e.printStackTrace()}
//    }
//
//    private fun releasePlayer() {
//        player?.release()
//        player = null
//    }
//
//    private fun pausePlayer() {
//        player?.playWhenReady = false
//        player?.playbackState
//    }
//
//    private fun resumePlayer() {
//        player?.playWhenReady = true
//        player?.playbackState
//    }
//
//    override fun onPause() {
//        super.onPause()
//        pausePlayer()
//    }
//
//    override fun onRestart() {
//        super.onRestart()
//        resumePlayer()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        releasePlayer()
//    }
//
//    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//        when (playbackState) {
//            Player.STATE_ENDED, Player.STATE_IDLE -> {
//            }
//            Player.STATE_READY -> {
//                binding.spinnerVideoDetails.visibility = View.GONE
//            }
//            Player.STATE_BUFFERING -> {
//                binding.spinnerVideoDetails.visibility = View.VISIBLE
//            }
//        }
//    }
//
//    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) { }
//
//    override fun onTracksChanged(trackGroups: TrackGroupArray?, tsa: TrackSelectionArray?) { }
//
//    override fun onLoadingChanged(isLoading: Boolean) { }
//
//    override fun onRepeatModeChanged(repeatMode: Int) { }
//
//    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) { }
//
//    override fun onPlayerError(error: ExoPlaybackException?) { }
//
//    override fun onPositionDiscontinuity(reason: Int) { }
//
//    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) { }
//
//    override fun onSeekProcessed() { }
//}
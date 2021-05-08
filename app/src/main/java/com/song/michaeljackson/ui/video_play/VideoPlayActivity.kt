package com.song.michaeljackson.ui.video_play

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.moodmi.utils.VideoPlayerConfig
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.song.michaeljackson.databinding.ActivityVideoPlayBinding
import com.song.michaeljackson.model.SongsResponseModel
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*
import com.song.michaeljackson.R

class VideoPlayActivity : AppCompatActivity(), Player.EventListener {

    companion object {
        const val VIDEO_DETAILS = "video_details"
    }

    private lateinit var binding: ActivityVideoPlayBinding

    private lateinit var video: SongsResponseModel.Result

    // Video Player
    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(VIDEO_DETAILS)) {
            // Video Details Object
            val vid = intent.extras?.getSerializable(VIDEO_DETAILS) as? SongsResponseModel.Result?
            // If Video Object is Null Finish Activity
            if(vid == null) {
                finish()
            }
            // Assign to Video Object
            video = vid as SongsResponseModel.Result
        }

        init()
    }

    private fun init() {

        // Set Video Meta Info
        Glide
            .with(this)
            .load(video.artworkUrl100)
            .centerCrop()
            .placeholder(R.mipmap.ic_launcher_round)
            .into(binding.songThumbnail)

        binding.txtTitle.text = video.trackName

        if (video.artistName != null){
            val name = MessageFormat.format("{0} : {1}",
                getString(R.string.author_name),
                video.artistName
            )
            binding.tvAuthorName.text = name
        }

        if (video.releaseDate != null){
            val formattedDate = formatDate(
                video.releaseDate,
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "dd MMM yyyy"
            )
            val date = MessageFormat.format("{0} : {1}",
                getString(R.string.date),
                formattedDate
            )
            binding.tvDate.text = date
        }

        if (video.trackPrice != 0.0){
            val price = MessageFormat.format("{0} : {1}$",
                getString(R.string.price),
                video.trackPrice
            )
            binding.tvPrice.text = price
        }

        // Set Video Player
        val loadControl: LoadControl = DefaultLoadControl(
            DefaultAllocator(true, 16),
            VideoPlayerConfig.MIN_BUFFER_DURATION,
            VideoPlayerConfig.MAX_BUFFER_DURATION,
            VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
            VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true
        )

        //  Currently Available Bandwidth Estimate
        val vtsFactory = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())

        // Track Selections
        val trackSelector: TrackSelector = DefaultTrackSelector(vtsFactory)

        // Create the player
        player = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(this), trackSelector, loadControl
        )

        binding.videoView.player = player

        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            this,getString(R.string.app_name), DefaultBandwidthMeter()
        )

        // Assign Default as Audio
        val mediaUrl = video.previewUrl

        Log.d(VIDEO_DETAILS,"url == $mediaUrl")

        try {
            // This is the MediaSource representing the media to be played.
            val videoSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mediaUrl))

            // Prepare the player with the source.
            player?.prepare(videoSource)
            player?.playWhenReady = true
            player?.addListener(this)
        } catch (e: Exception) {e.printStackTrace()}
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    /**
     * Pause song
     */
    private fun pausePlayer() {
        player?.playWhenReady = false
        player?.playbackState
    }

    /**
     * Resume song
     */
    private fun resumePlayer() {
        player?.playWhenReady = true
        player?.playbackState
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onRestart() {
        super.onRestart()
        resumePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_ENDED, Player.STATE_IDLE -> {
            }
            Player.STATE_READY -> {
                binding.spinnerVideoDetails.visibility = View.GONE
            }
            Player.STATE_BUFFERING -> {
                binding.spinnerVideoDetails.visibility = View.VISIBLE
            }
        }
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) { }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, tsa: TrackSelectionArray?) { }

    override fun onLoadingChanged(isLoading: Boolean) { }

    override fun onRepeatModeChanged(repeatMode: Int) { }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) { }

    override fun onPlayerError(error: ExoPlaybackException?) { }

    override fun onPositionDiscontinuity(reason: Int) { }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) { }

    override fun onSeekProcessed() { }

    /**
     * Format the given date.
     */
    private fun formatDate(
        dateStamp: String?,
        inputFormat: String?,
        outputFormat: String?
    ): String {
        return try {
            var format =
                SimpleDateFormat(inputFormat!!, Locale.ENGLISH)
            format.timeZone = TimeZone.getDefault()
            val newDate = format.parse(dateStamp!!)
            format = SimpleDateFormat(outputFormat!!, Locale.ENGLISH)

            format.timeZone = TimeZone.getDefault()
            format.format(newDate!!)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

    }
}
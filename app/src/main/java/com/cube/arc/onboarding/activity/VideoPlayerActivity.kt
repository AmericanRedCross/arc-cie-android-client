package com.cube.arc.onboarding.activity

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cube.arc.R
import com.cube.lib.util.bind
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.AssetDataSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSource.Factory

/**
 * Host activity that displays and plays the tutorial video
 */
class VideoPlayerActivity : AppCompatActivity()
{
	val videoPlayer by bind<SimpleExoPlayerView>(R.id.video_player)
	lateinit var player: SimpleExoPlayer

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setContentView(R.layout.video_player_view)
		val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(null)
		val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

		player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
		videoPlayer.requestFocus()
		videoPlayer.useController = false
		videoPlayer.player = player

		val dataSourceFactory: DataSource.Factory = object : Factory
		{
			override fun createDataSource(): DataSource
			{
				return AssetDataSource(this@VideoPlayerActivity)
			}
		}

		val videoSource = ExtractorMediaSource(Uri.parse("assets:///onboarding_video.mp4"), dataSourceFactory, DefaultExtractorsFactory(), null, null)

		player.prepare(videoSource)
		player.addListener(object: ExoPlayer.EventListener
		{
			override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?){}
			override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?){}
			override fun onPlayerError(error: ExoPlaybackException?){}
			override fun onLoadingChanged(isLoading: Boolean){}
			override fun onPositionDiscontinuity(){}
			override fun onTimelineChanged(timeline: Timeline?, manifest: Any?){}

			override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int)
			{
				if (playbackState == ExoPlayer.STATE_ENDED)
				{
					finish()
				}
			}
		})

		player.playWhenReady = true
	}

	override fun onDestroy()
	{
		super.onDestroy()

		player.release()
	}
}

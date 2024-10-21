package tv.vizbee.movidletv.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Renderer.MessageType
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import org.json.JSONObject
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.databinding.ActivityPlayerBinding
import tv.vizbee.movidletv.utils.ExoplayerUtils
import tv.vizbee.movidletv.vizbee.VizbeeXMessageParameter
import tv.vizbee.movidletv.vizbee.VizbeeXMessageType
import tv.vizbee.movidletv.vizbee.VizbeeXWrapper

class PlayerActivity : BaseActivity(), MediaSourceEventListener, Player.Listener {
    private lateinit var binding: ActivityPlayerBinding
    private var mediaSession: MediaSessionCompat? = null
    private var mediaSessionConnector: MediaSessionConnector? = null
    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mPlayer: ExoPlayer? = null
    private var mStartPosition = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 1. Initialize player
        initializeExoPlayer()

        // 2. Initialize MediaSession
        initializeMediaSession()
    }

    private fun initializeExoPlayer() {
        // 1. Create a default TrackSelector
        val trackSelector: TrackSelector = DefaultTrackSelector(this)

        // 2. Create a default LoadControl
        val loadControl: LoadControl = DefaultLoadControl()

        // 3. Create the player
        mPlayer = ExoPlayer.Builder(this).setTrackSelector(trackSelector).setLoadControl(loadControl).build()
        mPlayer?.addListener(this)

        // 4. Set up the player view
        binding.playerExoplayerView.player = mPlayer
        binding.playerExoplayerView.useController = true
    }

    private fun initializeMediaSession() {
        mediaSession = MediaSessionCompat(this, "ExoPlayerMediaSession").apply {
            isActive = true
            mediaSessionConnector = MediaSessionConnector(this).apply {
                setPlayer(mPlayer)
            }
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    mPlayer?.play()
                }

                override fun onPause() {
                    mPlayer?.pause()
                }

                override fun onSeekTo(pos: Long) {
                    mPlayer?.seekTo(pos)
                }

                override fun onStop() {
                    mPlayer?.stop()
                    finish()
                }
            })
            updatePlaybackState()
            updateMediaMetadata()
            mPlayer?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) = updatePlaybackState()
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) = updateMediaMetadata()
            })
        }
    }

    private fun updatePlaybackState() {
        mPlayer?.let {
            val state = when {
                it.isPlaying -> PlaybackStateCompat.STATE_PLAYING
                it.playbackState == Player.STATE_BUFFERING -> PlaybackStateCompat.STATE_BUFFERING
                else -> PlaybackStateCompat.STATE_PAUSED
            }

            val playbackState = PlaybackStateCompat.Builder()
                .setState(state, it.currentPosition, it.playbackParameters.speed)
                .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
            mediaSession?.setPlaybackState(playbackState)
        }
//            ?: kotlin.run { applicationContext.vizbeeAppLifecycleAdapter?.getAppReadyModel()?.deeplinkManager?.handleDeeplinkFailure() }
    }

    private fun updateMediaMetadata() {
        val mediaMetadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Sample Video Title")
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Sample Artist")
            .build()

        mediaSession?.setMetadata(mediaMetadata)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        if (mPlayer != null) {
            mPlayer?.playWhenReady = true
        }

        super.onResume()
        handleVideoIntent()
    }

    private fun handleVideoIntent() {
        intent.extras?.let { extras ->
            if (!extras.containsKey("duplicate")) {
                intent.putExtra("duplicate", true)
                val videoUrl: String? = extras.getString("videoUrl")
                val position: Long = extras.getLong("position", 0L)
                Log.i("PlayerActivity", videoUrl ?: "")
                videoUrl?.let { prepareVideo(it, position) }
            }
        }
    }

    override fun onStop() {
        super.onStop()

        if (null != mPlayer) {
            mPlayer?.release()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (binding.playerExoplayerView.dispatchMediaKeyEvent(event)) {
            true
        } else super.dispatchKeyEvent(event)
    }

    private fun prepareVideo(videoUrl: String?, position: Long) {
        Log.i(LOG_TAG, "prepareVideo: $videoUrl")

        // Save start position
        mStartPosition = position
        loadVideo(videoUrl, position)
    }

    private fun loadVideo(videoUrl: String?, position: Long) {
        val videoUri = Uri.parse(videoUrl)

        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(
                this,
                DefaultHttpDataSource.Factory().setUserAgent(ExoplayerUtils.getUserAgent(this))
            )

        // Produces Extractor instances for parsing the media data.
        val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()

        // This is the MediaSource representing the media to be played.
        val videoSource: MediaSource =
            ExoplayerUtils.buildMediaSource(videoUri, dataSourceFactory, mHandler, "", this)

        // Prepare the player with the source.
        mPlayer?.prepare(videoSource)
        mPlayer?.playWhenReady = false
        mPlayer?.addListener(this)
        resumeContent()
    }

    private fun resumeContent() {
        // Seek when start position is available
        if (mStartPosition != -1L) {
            mPlayer?.seekTo(mStartPosition.toLong())
            mStartPosition = -1
        }
        mPlayer?.playWhenReady = true

        binding.playerExoplayerView.visibility = View.VISIBLE
    }

    //-------------------------------------------------------------------------
    // ExoPlayer events
    //-------------------------------------------------------------------------

    @Deprecated("Deprecated in Java")
    override fun onLoadingChanged(isLoading: Boolean) {
    }

    @Deprecated("Deprecated in Java")
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == ExoPlayer.STATE_READY) {
            Log.d(LOG_TAG, "Player state changed: READY, PlayWhenReady = $playWhenReady")
        } else if (playbackState == ExoPlayer.STATE_ENDED) {
            Log.d(LOG_TAG, "Player state changed: ENDED")
            finish()
        } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
            Log.d(LOG_TAG, "Player state changed: BUFFERING")
        } else if (playbackState == ExoPlayer.STATE_IDLE) {
            Log.d(LOG_TAG, "Player state changed: IDLE")
        }
    }

    companion object {
        const val LOG_TAG = "PlayerActivity"
    }
}
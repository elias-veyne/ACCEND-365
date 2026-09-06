package com.accend.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.accend.app.R
import java.util.ArrayDeque

/**
 * Central audio hub for ACCEND.
 *
 * - Click / notification effects are fired through a [SoundPool] so rapid
 *   taps never block the UI thread.
 * - The loading-screen effect plays once via [MediaPlayer].
 * - Background music loops quietly for the whole session.
 *
 * The owning Activity drives the lifecycle (init on create, pause/resume on
 * the activity's pause/resume, release on destroy).
 */
object SoundManager {

    private const val BGM_VOLUME = 0.07f
    private const val SFX_VOLUME = 0.9f

    private var soundPool: SoundPool? = null
    private var clickSoundId = 0
    private var notificationSoundId = 0
    private val loadedSfx = mutableSetOf<Int>()
    private val pendingSfx = ArrayDeque<Int>()

    private var loadingPlayer: MediaPlayer? = null
    private var bgmPlayer: MediaPlayer? = null
    private var bgmPrepared = false
    private var bgmEnabled = false

    fun init(appContext: Context) {
        if (soundPool != null) return
        val context = appContext.applicationContext

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(attributes)
            .build()
        soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loadedSfx.add(sampleId)
                while (pendingSfx.remove(sampleId)) {
                    playLoadedSfx(sampleId)
                }
            }
        }
        clickSoundId = soundPool?.load(context, R.raw.click_sound, 1) ?: 0
        notificationSoundId = soundPool?.load(context, R.raw.notification_sound, 1) ?: 0

        loadingPlayer = MediaPlayer.create(context, R.raw.loading_sound)?.apply {
            setOnCompletionListener { }
        }

        bgmPlayer = MediaPlayer.create(context, R.raw.bg_music)?.apply {
            isLooping = true
            setVolume(BGM_VOLUME, BGM_VOLUME)
            bgmPrepared = true
            setOnPreparedListener {
                bgmPrepared = true
                if (bgmEnabled && !isPlaying) start()
            }
        }
    }

    fun playClick() = playSfx(clickSoundId)

    fun playNotification() = playSfx(notificationSoundId)

    fun playLoading() {
        loadingPlayer?.apply {
            seekTo(0)
            if (!isPlaying) start()
        }
    }

    fun stopLoading() {
        loadingPlayer?.takeIf { it.isPlaying }?.pause()
    }

    fun startBgm() {
        bgmEnabled = true
        bgmPlayer?.takeIf { bgmPrepared && !it.isPlaying }?.start()
    }

    fun pauseBgm() {
        bgmPlayer?.takeIf { it.isPlaying }?.pause()
    }

    fun resumeBgm() {
        if (bgmEnabled) {
            bgmPlayer?.takeIf { bgmPrepared && !it.isPlaying }?.start()
        }
    }

    fun release() {
        loadingPlayer?.release()
        loadingPlayer = null
        bgmPlayer?.release()
        bgmPlayer = null
        bgmPrepared = false
        bgmEnabled = false
        soundPool?.release()
        soundPool = null
        loadedSfx.clear()
        pendingSfx.clear()
    }

    private fun playSfx(soundId: Int) {
        if (soundId == 0) return
        if (soundId in loadedSfx) {
            playLoadedSfx(soundId)
        } else {
            pendingSfx.addLast(soundId)
        }
    }

    private fun playLoadedSfx(soundId: Int) {
        soundPool?.play(soundId, SFX_VOLUME, SFX_VOLUME, 1, 0, 1f)
    }
}

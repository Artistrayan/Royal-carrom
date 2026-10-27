package com.example.carrom.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object CarromSoundEngine {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isSoundEnabled: Boolean = true

    private const val SAMPLE_RATE = 22050

    fun playStrikerHit(powerPercent: Float) {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 120
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)
            val baseFreq = 220.0 + powerPercent * 180.0

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val decay = exp(-t * 35.0)
                // Wood body resonance with dual frequencies
                val wave = sin(2.0 * PI * baseFreq * t) * 0.7 +
                        sin(2.0 * PI * (baseFreq * 2.3) * t) * 0.3
                buffer[i] = (wave * decay * Short.MAX_VALUE * 0.85).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    fun playPieceClack(intensity: Float) {
        if (!isSoundEnabled) return
        scope.launch {
            val clamped = intensity.coerceIn(0.2f, 1.0f)
            val durationMs = 65
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)
            val baseFreq = 850.0 + (clamped * 450.0)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val decay = exp(-t * 60.0) // sharp quick wood click
                val wave = sin(2.0 * PI * baseFreq * t) * 0.8 +
                        sin(2.0 * PI * (baseFreq * 1.6) * t) * 0.2
                buffer[i] = (wave * decay * Short.MAX_VALUE * (0.5 + 0.4 * clamped)).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    fun playRailBounce() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 80
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)
            val baseFreq = 160.0

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val decay = exp(-t * 40.0)
                val wave = sin(2.0 * PI * baseFreq * t)
                buffer[i] = (wave * decay * Short.MAX_VALUE * 0.6).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    fun playPocketDrop() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 200
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                // Descending pitch representing falling into the deep net
                val freq = 420.0 - (t * 1200.0).coerceAtMost(280.0)
                val decay = exp(-t * 18.0)
                val wave = sin(2.0 * PI * freq * t)
                buffer[i] = (wave * decay * Short.MAX_VALUE * 0.8).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    fun playFoul() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 300
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val freq = 180.0 - (t * 80.0)
                val buzz = if ((i / 30) % 2 == 0) 1.0 else -1.0
                val decay = exp(-t * 6.0)
                buffer[i] = (buzz * decay * Short.MAX_VALUE * 0.5).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    fun playWinFanfare() {
        if (!isSoundEnabled) return
        scope.launch {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
            val noteDuration = 0.12
            val totalSamples = (SAMPLE_RATE * noteDuration * notes.size).toInt()
            val buffer = ShortArray(totalSamples)

            notes.forEachIndexed { noteIdx, freq ->
                val startIdx = (SAMPLE_RATE * noteDuration * noteIdx).toInt()
                val length = (SAMPLE_RATE * noteDuration).toInt()
                for (i in 0 until length) {
                    val sampleIdx = startIdx + i
                    if (sampleIdx < totalSamples) {
                        val t = i.toDouble() / SAMPLE_RATE
                        val decay = exp(-t * 4.0)
                        val wave = sin(2.0 * PI * freq * t)
                        buffer[sampleIdx] = (wave * decay * Short.MAX_VALUE * 0.7).toInt().toShort()
                    }
                }
            }
            playPcm(buffer)
        }
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            track.setNotificationMarkerPosition(buffer.size)
            track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(t: AudioTrack?) {
                    try {
                        t?.stop()
                        t?.release()
                    } catch (_: Exception) {}
                }
                override fun onPeriodicNotification(t: AudioTrack?) {}
            })
        } catch (_: Exception) {
            // AudioTrack failure fallback safely caught
        }
    }
}

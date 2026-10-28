package com.swifttap.x

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.SeekBar
import android.os.Handler
import android.os.Looper

class MainActivity : Activity() {

    private val handler = Handler(Looper.getMainLooper())
    private var running = false
    private var taps = 0
    private var interval = 100L
    private var durationSeconds = 10
    private var startedAt = 0L

    private lateinit var status: TextView
    private lateinit var counter: TextView
    private lateinit var speedLabel: TextView
    private lateinit var durationLabel: TextView

    private val task = object : Runnable {
        override fun run() {
            if (!running) return

            val elapsed = System.currentTimeMillis() - startedAt

            if (elapsed >= durationSeconds * 1000L) {
                stopTapping()
                return
            }

            taps++
            counter.text = "Test taps: $taps"
            handler.postDelayed(this, interval)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.rgb(18, 22, 32))
        }

        fun label(text: String, size: Float = 18f) =
            TextView(this).apply {
                this.text = text
                textSize = size
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                setPadding(8, 12, 8, 12)
            }

        val title = label("SwiftTap-X", 30f)
        status = label("Status: Ready")
        counter = label("Test taps: 0")
        speedLabel = label("Test interval: 100 ms")
        durationLabel = label("Duration: 10 seconds")

        val speed = SeekBar(this).apply {
            max = 99
            progress = 90
        }

        speed.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    bar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    interval = (100 - progress).toLong().coerceAtLeast(1)
                    speedLabel.text = "Test interval: $interval ms"
                }

                override fun onStartTrackingTouch(bar: SeekBar?) {}
                override fun onStopTrackingTouch(bar: SeekBar?) {}
            }
        )

        val duration = SeekBar(this).apply {
            max = 59
            progress = 9
        }

        duration.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    bar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    durationSeconds = progress + 1
                    durationLabel.text = "Duration: $durationSeconds seconds"
                }

                override fun onStartTrackingTouch(bar: SeekBar?) {}
                override fun onStopTrackingTouch(bar: SeekBar?) {}
            }
        )

        val start = Button(this).apply {
            text = "START TEST"
            setOnClickListener {
                if (!running) {
                    taps = 0
                    running = true
                    startedAt = System.currentTimeMillis()
                    status.text = "Status: Running local test"
                    handler.post(task)
                }
            }
        }

        val stop = Button(this).apply {
            text = "STOP"
            setOnClickListener { stopTapping() }
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(counter)
        layout.addView(speedLabel)
        layout.addView(speed)
        layout.addView(durationLabel)
        layout.addView(duration)
        layout.addView(start)
        layout.addView(stop)

        setContentView(layout)
    }

    private fun stopTapping() {
        running = false
        handler.removeCallbacks(task)
        status.text = "Status: Stopped"
    }

    override fun onDestroy() {
        stopTapping()
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}

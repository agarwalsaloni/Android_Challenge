package com.risingstar.androidchallenge.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.risingstar.androidchallenge.R
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class PlaySongsActivity : AppCompatActivity() {
        lateinit var imgPlaySong : ImageView
        lateinit var txtTitle : TextView
        lateinit var tvCurrentTime : TextView
        lateinit var tvTotalTime : TextView
        lateinit var seekBar: SeekBar
        private var mediaPlayer : MediaPlayer = MediaPlayer()
        private var handler = Handler()


        @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_play_songs)

                txtTitle = findViewById(R.id.txtTitle)
                tvCurrentTime = findViewById(R.id.tvCurrentTime)
                tvTotalTime = findViewById(R.id.tvTotalTime)
                imgPlaySong = findViewById(R.id.imgPlaySong)
                seekBar = findViewById(R.id.seekBar)

                txtTitle.text = intent.getStringExtra("Title")
                val songsUrl = intent.getStringExtra("Song url")!!

                seekBar.max = 100

                imgPlaySong.setOnClickListener {
                        if (mediaPlayer.isPlaying){
                                handler.removeCallbacks(updater)
                                mediaPlayer.pause()
                                imgPlaySong.setImageResource(R.drawable.ic_play_arrow)
                            }
                        else{
                                mediaPlayer.start()
                                imgPlaySong.setImageResource(R.drawable.ic_pause_circle)
                                updateSeekBar()
                            }
                    }

                prepareMediaPlayer(songsUrl)

                seekBar.setOnTouchListener(View.OnTouchListener { view: View, motionEvent: MotionEvent ->
                        val sekBar : SeekBar = view as SeekBar
                        val playPosition = (mediaPlayer.duration/100) * sekBar.progress
                        mediaPlayer.seekTo(playPosition)
                        tvCurrentTime.text = milliSecondToTimer(mediaPlayer.currentPosition.toLong())
                        return@OnTouchListener false
                    })

                mediaPlayer.setOnBufferingUpdateListener { mp, percent ->
                        seekBar.secondaryProgress = percent
                    }

                mediaPlayer.setOnCompletionListener {
                        seekBar.progress = 0
                        imgPlaySong.setImageResource(R.drawable.ic_play_arrow)
                        tvCurrentTime.text = "00:00"
                        tvTotalTime.text = "00:00"
                        mediaPlayer.reset()
                        prepareMediaPlayer(songsUrl)
                    }

            }

        private fun prepareMediaPlayer(audioUrl : String){
                try {
                        mediaPlayer.setDataSource(audioUrl)
                        mediaPlayer.prepare()
                        tvTotalTime.text = milliSecondToTimer(mediaPlayer.duration.toLong())
                    }
                catch (exception : Exception){
                        Toast.makeText(this@PlaySongsActivity,exception.message,Toast.LENGTH_SHORT).show()
                    }
            }
        var updater : Runnable = Runnable {
                updateSeekBar()
                val currentDuration= mediaPlayer.currentPosition.toLong()
                tvCurrentTime.text = milliSecondToTimer(currentDuration)
            }
        private fun milliSecondToTimer(millisecond: Long) : String {
                var timerString = ""
                val secondsString : String

                val hours = (millisecond/(1000*60*60)).toInt()
                val minutes = ((millisecond % (1000*60*60))/(1000*60)).toInt()
                val seconds = (((millisecond % (1000*60*60)) % (1000*60))/(1000)).toInt()
                if (hours>0){
                        timerString = "$hours:"
                    }
                if (seconds<10){
                        secondsString = "0$seconds"
                    }else{
                        secondsString = "$seconds"
                    }
                timerString = "$timerString$minutes:$secondsString"
                return timerString
            }
        private fun updateSeekBar(){
                if (mediaPlayer.isPlaying){
                        seekBar.progress = ((mediaPlayer.currentPosition/mediaPlayer.duration).toFloat() * 100).toInt()
                        handler.postDelayed(updater,1000)
                    }
            }
    }
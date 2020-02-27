package com.ukiuki.wordchanger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    private val handler = Handler()
    private val runnable = Runnable {

        // MainActivityへ遷移させる
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        // ここでfinish()を呼ばないとMainActivityでAndroidの戻るボタンを押すとスプラッシュ画面に戻ってしまう
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // スプラッシュ表示1000ms(1秒)後にrunnableを呼んでMeinActivityへ遷移させる
        handler.postDelayed(runnable,1000)
    }

    override fun onStop() {
        super.onStop()

        // スプラッシュ画面中にアプリを落とした時にはrunnableが呼ばれないようにする
        // これがないとアプリを消した後にまた表示される
        handler.removeCallbacks(runnable)
    }
}


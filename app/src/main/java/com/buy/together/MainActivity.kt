package com.buy.together

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buy.together.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    private lateinit var mainActivityBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)
    }

    fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

}
package com.buy.together

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.buy.together.databinding.ActivityMainBinding
import com.buy.together.ui.base.BaseFragment


class MainActivity : AppCompatActivity() {
    private lateinit var mainActivityBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)
    }

    fun restartActivity() {
        finish()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

fun <B : ViewBinding> BaseFragment<B>.restartActivity() {
    val activity = this.activity as MainActivity
    activity.restartActivity()
}
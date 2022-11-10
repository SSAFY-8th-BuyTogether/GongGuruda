package com.buy.together

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.buy.together.ui.view.board.BoardWritingFragment
import com.buy.together.ui.view.main.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: nav 연결하기
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_layout_main, MainFragment())
            .commit()
    }
}
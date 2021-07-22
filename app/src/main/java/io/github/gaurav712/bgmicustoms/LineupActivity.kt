package io.github.gaurav712.bgmicustoms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LineupActivity : AppCompatActivity() {

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lineup)

        textView = findViewById(R.id.textView)

        textView.text = intent.getStringExtra(EXTRA_MESSAGE)
    }
}
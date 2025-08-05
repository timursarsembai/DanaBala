package com.example.danabala

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupCardClicks()
    }

    private fun setupCardClicks() {
        findViewById<CardView>(R.id.cardMath).setOnClickListener {
            val intent = Intent(this, MathExercisesActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardReading).setOnClickListener {
            val intent = Intent(this, ReadingExercisesActivity::class.java)
            startActivity(intent)
        }
    }
}
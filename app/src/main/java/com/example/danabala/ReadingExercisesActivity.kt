package com.example.danabala

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ReadingExercisesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_exercises)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBackButton()
        setupExerciseCards()
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun setupExerciseCards() {
        findViewById<CardView>(R.id.cardLetters).setOnClickListener {
            val intent = Intent(this, LetterRecognitionActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardSyllables).setOnClickListener {
            // TODO: Слоги
        }

        findViewById<CardView>(R.id.cardWords).setOnClickListener {
            // TODO: Слова
        }

        findViewById<CardView>(R.id.cardSentences).setOnClickListener {
            // TODO: Предложения
        }
    }
}

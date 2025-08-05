package com.example.danabala

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MathExercisesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math_exercises)

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
        findViewById<CardView>(R.id.cardNumbers).setOnClickListener {
            val intent = Intent(this, NumberRecognitionActivity::class.java)
            startActivity(intent)
        }

        // Остальные карточки пока заглушки
        findViewById<CardView>(R.id.cardAddition).setOnClickListener {
            // TODO: Сложение
        }

        findViewById<CardView>(R.id.cardSubtraction).setOnClickListener {
            // TODO: Вычитание
        }

        findViewById<CardView>(R.id.cardCounting).setOnClickListener {
            // TODO: Счёт
        }
    }
}

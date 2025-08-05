package com.example.danabala

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*
import kotlin.random.Random

class LetterRecognitionActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var targetLetter = 'А'
    private var score = 0 // Правильные ответы с первого раза
    private var totalCorrectAnswers = 0 // Все правильные ответы (включая с повторными попытками)
    private var currentQuestion = 0
    private val totalQuestions = 33 // 33 буквы русского алфавита
    private var tts: TextToSpeech? = null
    private var isFirstInit = true // Флаг для первой инициализации
    private var hasTriedCurrentQuestion = false // Флаг для отслеживания попыток на текущем вопросе

    // Список всех вопросов (каждая буква по одному разу)
    private val questionLetters = mutableListOf<Char>()
    private var currentQuestionIndex = 0

    // Массив русских букв
    private val russianLetters = arrayOf(
        'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П',
        'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'
    )

    // Массив с фонетическим произношением букв
    private val letterPronunciation = mapOf(
        'А' to "а",
        'Б' to "бэ",
        'В' to "вэ",
        'Г' to "гэ",
        'Д' to "дэ",
        'Е' to "е",
        'Ё' to "ё",
        'Ж' to "жэ",
        'З' to "зэ",
        'И' to "ии",
        'Й' to "и краткое",
        'К' to "ка",
        'Л' to "эль",
        'М' to "эм",
        'Н' to "эн",
        'О' to "оо",
        'П' to "пэ",
        'Р' to "эр",
        'С' to "эс",
        'Т' to "тэ",
        'У' to "уу",
        'Ф' to "эф",
        'Х' to "ха",
        'Ц' to "цэ",
        'Ч' to "че",
        'Ш' to "ша",
        'Щ' to "ща",
        'Ъ' to "твердый знак",
        'Ы' to "ы",
        'Ь' to "мягкий знак",
        'Э' to "ээ",
        'Ю' to "ю",
        'Я' to "я"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letter_recognition)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализация TTS
        tts = TextToSpeech(this, this)

        setupBackButton()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.forLanguageTag("ru-RU")
            // Запускаем первый вопрос только при первой инициализации TTS
            if (isFirstInit) {
                generateQuestionSequence()
                startNewQuestion()
                isFirstInit = false
            }
        }
    }

    private fun setupBackButton() {
        findViewById<View>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun startNewQuestion() {
        if (currentQuestion >= totalQuestions) {
            showResultsScreen()
            return
        }

        currentQuestion++
        hasTriedCurrentQuestion = false // Сбрасываем флаг для нового вопроса

        // Скрываем loading и показываем игровой контент при первом вопросе
        if (currentQuestion == 1) {
            findViewById<View>(R.id.loadingContainer).visibility = View.GONE
            findViewById<View>(R.id.gameContainer).visibility = View.VISIBLE
        }

        // Берем букву из заранее сгенерированной последовательности
        targetLetter = questionLetters[currentQuestionIndex]
        currentQuestionIndex++

        // Формируем вопрос с фонетическим произношением
        val letterPronunciationText = letterPronunciation[targetLetter] ?: targetLetter.toString()
        val questionText = "Найди букву $letterPronunciationText"

        // Озвучиваем вопрос
        tts?.speak(questionText, TextToSpeech.QUEUE_FLUSH, null, "question")

        // Настраиваем кнопку динамика для повторного озвучивания
        findViewById<View>(R.id.speakerButton).setOnClickListener {
            tts?.speak(questionText, TextToSpeech.QUEUE_FLUSH, null, "repeat_question")
        }

        // Обновляем прогресс-бар
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.progress = (currentQuestion * 100) / totalQuestions

        // Генерируем 4 случайные буквы, одна из которых правильная
        val letters = generateLetterOptions(targetLetter)

        // Находим карточки
        val cards = listOf(
            findViewById<CardView>(R.id.card1),
            findViewById<CardView>(R.id.card2),
            findViewById<CardView>(R.id.card3),
            findViewById<CardView>(R.id.card4)
        )

        val letterTexts = listOf(
            findViewById<TextView>(R.id.letter1),
            findViewById<TextView>(R.id.letter2),
            findViewById<TextView>(R.id.letter3),
            findViewById<TextView>(R.id.letter4)
        )

        // Заполняем карточки
        for (i in 0..3) {
            letterTexts[i].text = letters[i].toString()

            // Сбрасываем цвет карточки
            cards[i].setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.white))

            // Добавляем анимацию появления
            animateCardEntrance(cards[i], i * 100L)

            cards[i].setOnClickListener {
                checkAnswer(letters[i], cards[i])
            }
        }
    }

    private fun animateCardEntrance(card: CardView, delay: Long) {
        card.alpha = 0f
        card.scaleX = 0.5f
        card.scaleY = 0.5f

        card.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setStartDelay(delay)
            .setInterpolator(BounceInterpolator())
            .start()
    }

    private fun generateLetterOptions(correct: Char): List<Char> {
        val options = mutableSetOf<Char>()
        options.add(correct)

        // Добавляем 3 неправильных варианта из русского алфавита
        while (options.size < 4) {
            val randomLetter = russianLetters[Random.nextInt(russianLetters.size)]
            options.add(randomLetter)
        }

        return options.shuffled()
    }

    private fun checkAnswer(selectedLetter: Char, selectedCard: CardView) {
        // Отключаем все карточки от нажатий
        disableAllCards()

        if (selectedLetter == targetLetter) {
            // Правильный ответ
            totalCorrectAnswers++

            // Засчитываем правильный ответ с первого раза только если не было попыток
            if (!hasTriedCurrentQuestion) {
                score++
            }

            animateCorrectAnswer(selectedCard)
            tts?.speak("Молодец!", TextToSpeech.QUEUE_FLUSH, null, "correct")

            // Переходим к следующему вопросу через 2 секунды
            findViewById<View>(R.id.card1).postDelayed({
                startNewQuestion()
            }, 2000)
        } else {
            // Неправильный ответ - отмечаем что была попытка
            hasTriedCurrentQuestion = true

            animateWrongAnswer(selectedCard)
            tts?.speak("Попробуй ещё раз!", TextToSpeech.QUEUE_FLUSH, null, "wrong")

            // Через 2 секунды включаем карточки обратно
            findViewById<View>(R.id.card1).postDelayed({
                enableAllCards()
                resetCardColors()
            }, 2000)
        }
    }

    private fun enableAllCards() {
        val cards = listOf(
            findViewById<CardView>(R.id.card1),
            findViewById<CardView>(R.id.card2),
            findViewById<CardView>(R.id.card3),
            findViewById<CardView>(R.id.card4)
        )

        cards.forEach { it.isClickable = true }
    }

    private fun resetCardColors() {
        val cards = listOf(
            findViewById<CardView>(R.id.card1),
            findViewById<CardView>(R.id.card2),
            findViewById<CardView>(R.id.card3),
            findViewById<CardView>(R.id.card4)
        )

        cards.forEach {
            it.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        }
    }

    private fun disableAllCards() {
        val cards = listOf(
            findViewById<CardView>(R.id.card1),
            findViewById<CardView>(R.id.card2),
            findViewById<CardView>(R.id.card3),
            findViewById<CardView>(R.id.card4)
        )

        cards.forEach { it.isClickable = false }
    }

    private fun animateCorrectAnswer(card: CardView) {
        // Зеленый цвет для правильного ответа
        card.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))

        // Анимация увеличения и уменьшения
        val scaleUpX = ObjectAnimator.ofFloat(card, "scaleX", 1f, 1.3f)
        val scaleUpY = ObjectAnimator.ofFloat(card, "scaleY", 1f, 1.3f)
        val scaleDownX = ObjectAnimator.ofFloat(card, "scaleX", 1.3f, 1f)
        val scaleDownY = ObjectAnimator.ofFloat(card, "scaleY", 1.3f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.play(scaleUpX).with(scaleUpY)
        animatorSet.play(scaleDownX).with(scaleDownY).after(scaleUpX)
        animatorSet.duration = 200
        animatorSet.start()
    }

    private fun animateWrongAnswer(card: CardView) {
        // Красный цвет для неправильного ответа
        card.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))

        // Анимация тряски
        val shake = ObjectAnimator.ofFloat(card, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        shake.duration = 500
        shake.start()
    }

    private fun showResultsScreen() {
        val intent = Intent(this, ResultsActivity::class.java)
        intent.putExtra("score", score)
        intent.putExtra("total", totalQuestions)
        intent.putExtra("parentSection", "reading") // Указываем, что пришли из раздела чтения
        startActivity(intent)
        finish()
    }

    private fun generateQuestionSequence() {
        // Создаем список: каждая буква по одному разу
        val letters = mutableListOf<Char>()
        letters.addAll(russianLetters)

        // Перемешиваем список
        letters.shuffle()

        questionLetters.clear()
        questionLetters.addAll(letters)
        currentQuestionIndex = 0
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

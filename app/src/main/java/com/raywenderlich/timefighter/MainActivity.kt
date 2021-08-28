package com.raywenderlich.timefighter
import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
class MainActivity : AppCompatActivity() {


    private val TAG = MainActivity::class.java.simpleName

    private lateinit var gameScoreTextView: TextView
    private lateinit var timeRemainingTextView: TextView
    private lateinit var tapMeButton: Button

    private var score = 0

    private var gameStarted = false
    private lateinit var countDownTimer: CountDownTimer
    private var initialCountDown: Long = 10000
    private var countDownInterval: Long = 1000
    private var timeRemaining = 10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate called. Score is: $score")
        // connect views to variables
        // 1
        gameScoreTextView = findViewById(R.id.game_score_text_view)
        timeRemainingTextView = findViewById(R.id.time_remaining_text_view)
        tapMeButton = findViewById(R.id.tap_me_button)
        // 2
        tapMeButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this,
                R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }

        if(savedInstanceState != null){
            score = savedInstanceState.getInt(SCORE_KEY)
            timeRemaining = savedInstanceState.getInt(TIME_REMAINING_KEY)
            restoreGame()
        } else{
            resetGame()
        }
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putInt(TIME_REMAINING_KEY, timeRemaining)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time Remaining: $timeRemaining")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy called.")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about_item) {
            showInfo()
        }
        return true
    }

    private fun showInfo() {
        val dialogTitle = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    private fun incrementScore() {
        if (!gameStarted){
            startGame()
        }

        score++

        val newScore = getString(R.string.your_score, score)
        gameScoreTextView.text = newScore
    }

    private fun resetGame() {
        //1
        score = 0;

        val initialScore = getString(R.string.your_score, score)
        gameScoreTextView.text = initialScore

        val initialTimeRemaining = getString(R.string.time_remaining, 10)
        timeRemainingTextView.text = initialTimeRemaining

        //2
        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished.toInt() / 1000

                val timeRemainingString = getString(R.string.time_remaining, timeRemaining)
                timeRemainingTextView.text = timeRemainingString
            }

            override fun onFinish(){
                endGame()
            }
        }

        //4
        gameStarted = false
    }

    private fun restoreGame(){
        val restoredScore = getString(R.string.your_score, score)
        gameScoreTextView.text = restoredScore

        val restoredTime = getString(R.string.time_remaining, timeRemaining)
        timeRemainingTextView.text = restoredTime

        countDownTimer = object : CountDownTimer((timeRemaining * 1000).toLong(), countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished.toInt() / 1000

                val timeRemainingString = getString(R.string.time_remaining, timeRemaining)
                timeRemainingTextView.text = timeRemainingString
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        gameStarted = true

    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame() {
        Toast.makeText(this, getString(R.string.game_over_message, score), Toast.LENGTH_LONG).show()
        resetGame()
    }


    companion object{
        private const val SCORE_KEY = "SCORE KEY"

        private const val TIME_REMAINING_KEY = "TIME_REMAINING_KEY"
    }
}
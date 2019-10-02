package com.mcdt.quizproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mcdt.quizproject.Model.HighScore;
import com.mcdt.quizproject.Model.Question;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Engine.EngineInterface
{
    private Engine m_engine;

    private Button m_buttonPlay;
    private Button m_buttonScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_buttonPlay = findViewById(R.id.buttonPlay);
        m_buttonPlay.setOnClickListener(this);

        m_buttonScores = findViewById(R.id.buttonScores);
        m_buttonScores.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get a fresh instance of engine.
        Engine.resetInstance();
        m_engine = Engine.getInstance(MainActivity.this);
        m_engine.setCallback(this);
    }

    @Override
    public void onClick(View v) {
        if (v == m_buttonPlay) {
            m_engine.startGameSession();
        } else if (v == m_buttonScores) {
            startHighScoreActivity();
        }
    }

    public void startGameSessionActivity() {
        Intent intent = new Intent(this, GameSessionActivity.class);
        startActivity(intent);
    }

    public void startHighScoreActivity() {
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }

    @Override
    public void onGameTimerTick(boolean finished, int relativeProgress) {
        // unused
    }

    @Override
    public void onParseResponseStartGameSession(final String sessionId) {
        if (sessionId != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startGameSessionActivity();
                }
            });
        } else {
            Log.d("SESSION", "onParseResponseStartGameSession: sessionId is null");
            // error handling here
        }
    }

    @Override
    public void onParseResponseGetRandomQuestion(Question question) {
        // unused
    }

    @Override
    public void onParseResponseCheckCorrectAnswer(final boolean status, final int currentScore) {
        // unused
    }

    @Override
    public void onParseResponsePostHighScoreInfo() {
        // unused
    }

    @Override
    public void onParseResponseGetAllHighScores(final List<HighScore> highScores) {
        // unused
    }
}
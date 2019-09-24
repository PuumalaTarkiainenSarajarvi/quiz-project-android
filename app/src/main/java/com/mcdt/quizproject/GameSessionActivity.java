package com.mcdt.quizproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GameSessionActivity extends AppCompatActivity
{
    private ProgressBar m_progressBarGameTime;
    private TextView m_textViewCurrentScore;
    private TextView m_textViewQuestionDifficulty;
    private TextView m_textViewQuestionCategory;
    private TextView m_textViewQuestion;
    private ListView m_listViewAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_session);

        m_textViewCurrentScore = findViewById(R.id.textViewCurrentScore);
        m_textViewQuestionDifficulty = findViewById(R.id.textViewQuestionDifficulty);
        m_textViewQuestionCategory = findViewById(R.id.textViewQuestionCategory);
        m_textViewQuestion = findViewById(R.id.textViewQuestion);
        m_listViewAnswers = findViewById(R.id.listViewAnswers);
    }
}

package com.mcdt.quizproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mcdt.quizproject.Adapter.AnswerArrayAdapter;
import com.mcdt.quizproject.Model.Question;

import java.util.Timer;
import java.util.TimerTask;

public class GameSessionActivity extends AppCompatActivity implements Engine.EngineInterface
{
    private static final int INITIAL_GAME_TIME = 60;
    private static final int CREDENTIALS_REQUEST_CODE = 1;

    private Engine m_engine;
    private AnswerArrayAdapter m_adapter;

    private int m_gameTimeLeft;
    private Question m_currentQuestion = null;

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

        m_progressBarGameTime = findViewById(R.id.progressBarGameTime);
        m_textViewCurrentScore = findViewById(R.id.textViewCurrentScore);
        m_textViewQuestionDifficulty = findViewById(R.id.textViewQuestionDifficulty);
        m_textViewQuestionCategory = findViewById(R.id.textViewQuestionCategory);
        m_textViewQuestion = findViewById(R.id.textViewQuestion);
        m_listViewAnswers = findViewById(R.id.listViewAnswers);

        m_textViewCurrentScore.setText(String.format(getString(R.string.current_score), 0));
        startGameTimer();

        m_engine = Engine.getInstance(GameSessionActivity.this);
        m_engine.setCallback(this);

        m_engine.getRandomQuestion();

        m_listViewAnswers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (m_currentQuestion != null) {
                    String questionId = m_currentQuestion.getId();
                    String answer = m_listViewAnswers.getItemAtPosition(position).toString();

                    m_engine.checkCorrectAnswer(questionId, answer);
                }
            }
        });
    }

    public void startGameTimer() {
        m_gameTimeLeft = INITIAL_GAME_TIME;

        final Handler handler = new Handler();
        final Timer timer = new Timer(false);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        m_gameTimeLeft--;
                        // set progress as percentage
                        float relativeProgress = (float) m_gameTimeLeft
                                / (float) INITIAL_GAME_TIME * 100.f;

                        m_progressBarGameTime.setProgress((int) relativeProgress);
                        if (m_gameTimeLeft <= 0) {
                            openCredentialsActivity();
                            timer.cancel();
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    public void openCredentialsActivity() {
        Intent intent = new Intent(this, CredentialsActivity.class);
        intent.putExtra("labelText", getString(R.string.game_over)
                + " Score: " + m_engine.getPlayerScore());
        startActivityForResult(intent, CREDENTIALS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == CredentialsActivity.CREDENTIALS_RESULT_CODE_OK) {
            String nickname = data.getStringExtra("nickname");
            String email = data.getStringExtra("email");
            m_engine.postHighScoreInfo(nickname, email);
        } else {
            finish();
        }
    }

    @Override
    public void onParseResponseStartGameSession(String sessionId) {
        // unused
    }

    @Override
    public void onParseResponseGetRandomQuestion(final Question question) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_textViewQuestion.setText(question.getQuestion());
                m_textViewQuestionDifficulty.setText(String.format(
                        getResources().getString(R.string.difficulty), question.getDifficulty()));

                m_textViewQuestionCategory.setText(String.format(
                        getResources().getString(R.string.category), question.getCategory()));

                m_adapter = new AnswerArrayAdapter(
                        GameSessionActivity.this, question.getAnswers());

                m_listViewAnswers.setAdapter(m_adapter);

                // set reference to current question on display
                m_currentQuestion = question;
            }
        });
    }

    @Override
    public void onParseResponseCheckCorrectAnswer(final boolean status, final int currentScore) {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                m_textViewCurrentScore.setText(String.format(
                        getString(R.string.current_score), currentScore));

                if (status)
                    m_textViewCurrentScore.setTextColor(Color.GREEN);
                else
                    m_textViewCurrentScore.setTextColor(Color.RED);

                // now request a new question
                m_engine.getRandomQuestion();
            }
        });
    }

    @Override
    public void onParseResponsePostHighScoreInfo() {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                GameSessionActivity.this.finish();
            }
        });
    }
}

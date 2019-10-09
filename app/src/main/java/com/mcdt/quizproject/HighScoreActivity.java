package com.mcdt.quizproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.mcdt.quizproject.Adapter.HighScoreArrayAdapter;
import com.mcdt.quizproject.Model.HighScore;
import com.mcdt.quizproject.Model.Question;

import java.util.List;

public class HighScoreActivity extends AppCompatActivity implements Engine.EngineInterface
{
    private SharedPreferences m_pref;
    private Engine m_engine;
    private HighScoreArrayAdapter m_adapter;

    private ListView m_listViewHighScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        m_listViewHighScores = findViewById(R.id.listViewHighScores);

        m_pref = getApplicationContext().getSharedPreferences("Identifier", 0);

        m_engine = Engine.getInstance(HighScoreActivity.this);
        m_engine.setCallback(this);

        m_engine.getAllHighScores();
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radioButtonPersonalBest:
                if (checked)
                    tryGetPersonalBests();
                    break;
            case R.id.radioButtonWorldBest:
                if (checked)
                    m_engine.getAllHighScores();
                    break;
        }
    }

    public void tryGetPersonalBests() {
        String email = m_pref.getString("email", null);
        if (email != null) {
            m_engine.getPersonalBests(email);
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.email_null_toast), Toast.LENGTH_LONG).show();
        }
    }

    public void showHighScores(final List<HighScore> highScores) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_adapter = new HighScoreArrayAdapter(
                        HighScoreActivity.this, highScores);

                m_listViewHighScores.setAdapter(m_adapter);
            }
        });
    }

    @Override
    public void onParseResponseGetAllHighScores(final List<HighScore> highScores) {
        showHighScores(highScores);
    }

    @Override
    public void onParseResponseGetPersonalBests(final List<HighScore> highScores) {
        showHighScores(highScores);
    }

/*
    ##### UNUSED #####
 */
    @Override
    public void onGameTimerTick(boolean finished, int relativeProgress) {
        // unused
    }

    @Override
    public void onParseResponseStartGameSession(String sessionId) {
        // unused
    }

    @Override
    public void onParseResponseGetRandomQuestion(Question question) {
        // unused
    }

    @Override
    public void onParseResponseCheckCorrectAnswer(boolean status, int currentScore) {
        // unused
    }

    @Override
    public void onParseResponsePostHighScoreInfo() {
        // unused
    }
}

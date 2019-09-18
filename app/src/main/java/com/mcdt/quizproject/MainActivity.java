package com.mcdt.quizproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
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
    public void onClick(View v) {
        if (v == m_buttonPlay) {

        } else if (v == m_buttonScores) {

        }
    }
}
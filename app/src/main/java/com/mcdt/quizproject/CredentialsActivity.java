package com.mcdt.quizproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CredentialsActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final int CREDENTIALS_RESULT_CODE_OK = 1;

    private SharedPreferences m_pref;
    private SharedPreferences.Editor m_editor;

    private TextView m_textViewCredentialsLabel;
    private EditText m_editTextNickname;
    private EditText m_editTextEmail;
    private Button m_buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);

        m_textViewCredentialsLabel = findViewById(R.id.textViewCredentialsLabel);
        m_editTextNickname = findViewById(R.id.editTextNickname);
        m_editTextEmail = findViewById(R.id.editTextEmail);
        m_buttonSubmit = findViewById(R.id.buttonSubmit);

        m_buttonSubmit.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            m_textViewCredentialsLabel.setText(extras.getString("labelText"));

        m_pref = getApplicationContext().getSharedPreferences("Identifier", 0);

        String nickname = m_pref.getString("nickname", null);
        String email = m_pref.getString("email", null);

        if (!(nickname == null || email == null)) {
            m_editTextNickname.setText(nickname);
            m_editTextEmail.setText(email);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == m_buttonSubmit) {
            String nickname = m_editTextNickname.getText().toString();
            String email = m_editTextEmail.getText().toString();

            if (!(nickname.isEmpty() || email.isEmpty())) {
                m_editor = m_pref.edit();

                m_editor.putString("nickname", nickname);
                m_editor.putString("email", email);
                m_editor.apply();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("nickname",nickname);
                resultIntent.putExtra("email", email);
                setResult(CREDENTIALS_RESULT_CODE_OK, resultIntent);
                finish();
            }
        }
    }
}

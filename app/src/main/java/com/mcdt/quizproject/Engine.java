package com.mcdt.quizproject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.mcdt.quizproject.Model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Engine implements RestAsyncTask.OnRequestProgressUpdate
{
    // API endpoints declaration
    private static final String API_BASE_URL = "http://10.0.2.2:5000";
    private static final String GET_RANDOM_QUESTION = "/api/get_random_question";
    private static final String GET_ALL_HIGH_SCORES = "/api/get_all_high_scores";
    private static final String POST_HIGH_SCORE_INFO = "/api/post_high_score_info";
    private static final String CHECK_CORRECT_ANSWER = "/api/check_correct_answer";
    private static final String START_GAME_SESSION = "/api/start_game_session";

    private static Engine m_instance = null;
    private static Context m_context;
    private EngineInterface m_callback;

    private String m_sessionId = null;
    private int m_playerScore;

    private Engine() {}

    // static method to create instance of Engine class
    public static Engine getInstance(Context context) {
        if (m_instance == null)
            m_instance = new Engine();

        m_context = context;
        return m_instance;
    }

    public static void resetInstance() {
        m_instance = null;
    }

    public void setCallback(final EngineInterface callback) {
        m_callback = callback;
    }

    public interface EngineInterface
    {
        void onParseResponseStartGameSession(final String sessionId);
        void onParseResponseGetRandomQuestion(final Question question);
        void onParseResponseCheckCorrectAnswer(final boolean status, final int currentScore);
        void onParseResponsePostHighScoreInfo();
    }

    public int getPlayerScore() {
        return m_playerScore;
    }

    public void startGameSession() {
        Log.d("ENGINE", "startGameSession: called");
        RestAsyncTask restAsyncTask = new RestAsyncTask(m_context, this);
        restAsyncTask.addRequestToQueue(
                API_BASE_URL, START_GAME_SESSION, Request.Method.POST);
    }

    public void parseResponseStartGameSession(final JSONObject response) {
        try {
            m_sessionId = response.getString("session_id");
        } catch (final JSONException e) {
            Log.e("JSON", "JSON parsing error: " + e.getMessage());
        }
        m_callback.onParseResponseStartGameSession(m_sessionId);
    }

    public void getRandomQuestion() {
        Log.d("ENGINE", "getRandomQuestion: called");
        RestAsyncTask restAsyncTask = new RestAsyncTask(m_context, this);
        restAsyncTask.addRequestToQueue(
                API_BASE_URL, GET_RANDOM_QUESTION, Request.Method.GET, m_sessionId);
    }

    public void parseResponseGetRandomQuestion(final JSONObject response) {
        try {
            String tempId = response.getString("_id");
            String tempQuestion = response.getString("question");
            String tempCategory = response.getString("category");
            String tempDifficulty = response.getString("difficulty");
            List<String> tempAnswers = new ArrayList<>();

            JSONArray answers = response.getJSONArray("answers");
            for (int i = 0; i < answers.length(); i++) {
                tempAnswers.add(answers.getString(i));
            }
            m_callback.onParseResponseGetRandomQuestion(new Question(tempId, tempQuestion, tempAnswers, tempCategory, tempDifficulty));
        } catch (final JSONException e) {
            Log.e("JSON", "JSON parsing error: " + e.getMessage());
        }
    }

    public void checkCorrectAnswer(final String questionId, final String answer) {
        Log.d("ENGINE", "checkCorrectAnswer: called");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("question_id", questionId);
            jsonObject.put("correct_answer", answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        RestAsyncTask restAsyncTask = new RestAsyncTask(m_context, this);
        restAsyncTask.addRequestToQueue(
                API_BASE_URL, CHECK_CORRECT_ANSWER, Request.Method.POST, requestBody, m_sessionId);
    }

    public void parseResponseCheckCorrectAnswer(final JSONObject response) {
        try {
            boolean status = response.getBoolean("status");
            int currentScore = response.getInt("current_score");

            m_callback.onParseResponseCheckCorrectAnswer(status, currentScore);
            m_playerScore = currentScore;
        } catch (final JSONException e) {
            Log.e("JSON", "JSON parsing error: " + e.getMessage());
        }
    }

    public void postHighScoreInfo(final String nickname, final String email) {
        Log.d("ENGINE", "postHighScoreInfo: called");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nickname", nickname);
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        RestAsyncTask restAsyncTask = new RestAsyncTask(m_context, this);
        restAsyncTask.addRequestToQueue(
                API_BASE_URL, POST_HIGH_SCORE_INFO, Request.Method.POST, requestBody, m_sessionId);
    }

    public void parseResponsePostHighScoreInfo(final JSONObject response) {
        m_callback.onParseResponsePostHighScoreInfo();
    }

    @Override
    public void requestDone(final String requestId, JSONObject response) {
        Log.d("VOLLEY", "requestDone: " + response.toString());
        switch (requestId) {
            case START_GAME_SESSION:
                parseResponseStartGameSession(response);
                break;
            case GET_RANDOM_QUESTION:
                parseResponseGetRandomQuestion(response);
                break;
            case CHECK_CORRECT_ANSWER:
                parseResponseCheckCorrectAnswer(response);
                break;
            case POST_HIGH_SCORE_INFO:
                parseResponsePostHighScoreInfo(response);
                break;
            default:
                break;
        }
    }
}

package com.mcdt.quizproject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.mcdt.quizproject.Model.HighScore;
import com.mcdt.quizproject.Model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Engine implements RestAsyncTask.OnRequestProgressUpdate
{
    private static final int INITIAL_GAME_TIME = 60;

    // API endpoints declaration
    private static final String API_BASE_URL = "https://quiz-project-api.herokuapp.com";
    private static final String GET_RANDOM_QUESTION = "/api/get_random_question";
    private static final String GET_ALL_HIGH_SCORES = "/api/get_all_high_scores";
    private static final String GET_PERSONAL_BESTS = "/api/get_personal_bests";
    private static final String POST_HIGH_SCORE_INFO = "/api/post_high_score_info";
    private static final String CHECK_CORRECT_ANSWER = "/api/check_correct_answer";
    private static final String START_GAME_SESSION = "/api/start_game_session";

    private static Engine m_instance = null;
    private static Context m_context;
    private EngineInterface m_callback;

    private Timer m_timer = null;

    private String m_sessionId = null;
    private int m_playerScore;
    private int m_gameTimeLeft;

    private Engine() {}

    // static method to create instance of Engine class
    public static Engine getInstance(Context context) {
        if (m_instance == null)
            m_instance = new Engine();

        m_context = context;
        return m_instance;
    }

    public static void resetInstance() {
        if (getInstance(m_context).m_timer != null)
            getInstance(m_context).m_timer.cancel();
        m_instance = null;
    }

    public void setCallback(final EngineInterface callback) {
        m_callback = callback;
    }

    public interface EngineInterface
    {
        void onGameTimerTick(final boolean finished, final int relativeProgress);
        void onParseResponseStartGameSession(final String sessionId);
        void onParseResponseGetRandomQuestion(final Question question);
        void onParseResponseCheckCorrectAnswer(final boolean status, final int currentScore);
        void onParseResponsePostHighScoreInfo();
        void onParseResponseGetAllHighScores(final List<HighScore> highScores);
        void onParseResponseGetPersonalBests(final List<HighScore> highScores);
    }

    public int getPlayerScore() {
        return m_playerScore;
    }

    public void startGameSession() {
        Log.d("ENGINE", "startGameSession: called");
        RestAsyncTask restAsyncTask = new RestAsyncTask(m_context, this);
        restAsyncTask.addObjectRequestToQueue(
                API_BASE_URL, START_GAME_SESSION, Request.Method.POST);
    }

    public void startGameTimer() {
        m_gameTimeLeft = INITIAL_GAME_TIME;

        final Handler handler = new Handler();
        m_timer = new Timer(false);

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

                        m_callback.onGameTimerTick(false, (int) relativeProgress);

                        if (m_gameTimeLeft <= 0) {
                            m_callback.onGameTimerTick(true, (int) relativeProgress);
                            m_timer.cancel();
                        }
                    }
                });
            }
        };
        m_timer.schedule(timerTask, 1000, 1000);
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
        restAsyncTask.addObjectRequestToQueue(
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
        restAsyncTask.addObjectRequestToQueue(
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
        restAsyncTask.addObjectRequestToQueue(
                API_BASE_URL, POST_HIGH_SCORE_INFO, Request.Method.POST, requestBody, m_sessionId);
    }

    public void parseResponsePostHighScoreInfo(final JSONObject response) {
        m_callback.onParseResponsePostHighScoreInfo();
    }

    public void getAllHighScores() {
        Log.d("ENGINE", "getAllHighScores: called");
        RestAsyncTask restAsyncTask = new RestAsyncTask(m_context, this);
        restAsyncTask.addArrayRequestToQueue(
                API_BASE_URL, GET_ALL_HIGH_SCORES, Request.Method.GET);
    }

    public void parseResponseGetAllHighScores(final JSONArray response) {
        try {
            List<HighScore> highScores = new ArrayList<>();
            for (int i = 0; i < response.length(); ++i) {
                JSONObject child = response.getJSONObject(i);
                HighScore tempScore = new HighScore(
                        String.valueOf(i) + ".",
                        child.getString("nickname"),
                        child.getString("score"));

                highScores.add(tempScore);
            }
            Collections.sort(highScores, new Comparator<HighScore>() {
                @Override
                public int compare(HighScore h1, HighScore h2) {
                    return Integer.valueOf(h2.getItems().get(2))
                            .compareTo(Integer.valueOf(h1.getItems().get(2)));
                }
            });
            // add new values for index row
            for (HighScore score : highScores)
                score.setItemValue(0,
                        String.valueOf(highScores.indexOf(score) + 1) + ".");

            m_callback.onParseResponseGetAllHighScores(highScores);
        } catch (final JSONException e) {
            Log.e("JSON", "JSON parsing error: " + e.getMessage());
        }
    }

    public void getPersonalBests(final String email) {
        Log.d("ENGINE", "getPersonalBests: called");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        RestAsyncTask restAsyncTask = new RestAsyncTask(m_context, this);
        restAsyncTask.addArrayRequestToQueue(
                API_BASE_URL, GET_PERSONAL_BESTS, Request.Method.POST, requestBody);
    }

    public void parseResponseGetPersonalBests(final JSONArray response) {
        try {
            List<HighScore> highScores = new ArrayList<>();

            JSONObject child = response.getJSONObject(0);
            JSONArray tenBestScores = child.getJSONArray("tenBestScores");

            for (int i = 0; i < tenBestScores.length(); i++) {
                JSONObject scoreChild = tenBestScores.getJSONObject(i);
                // Format only date to be shown
                String date = scoreChild.getString("date");
                int index = date.indexOf("T");

                HighScore tempScore = new HighScore(
                        String.valueOf(i) + ".",
                        date.substring(0, index).replace("-", "."),
                        scoreChild.getString("score"));

                highScores.add(tempScore);
            }
            Collections.sort(highScores, new Comparator<HighScore>() {
                @Override
                public int compare(HighScore h1, HighScore h2) {
                    return Integer.valueOf(h2.getItems().get(2))
                            .compareTo(Integer.valueOf(h1.getItems().get(2)));
                }
            });
            // add new values for index row
            for (HighScore score : highScores)
                score.setItemValue(0,
                        String.valueOf(highScores.indexOf(score) + 1) + ".");

            // Add a "heading" containing user information
            highScores.add(0, new HighScore(child.getString("nickname"),
                    "", child.getString("email")));

            m_callback.onParseResponseGetPersonalBests(highScores);
        } catch (final JSONException e) {
            Log.e("JSON", "JSON parsing error: " + e.getMessage());
        }
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

    @Override
    public void requestDone(final String requestId, JSONArray response) {
        Log.d("VOLLEY", "requestDone: " + response.toString());
        switch (requestId) {
            case GET_ALL_HIGH_SCORES:
                parseResponseGetAllHighScores(response);
            case GET_PERSONAL_BESTS:
                parseResponseGetPersonalBests(response);
            default:
                break;
        }
    }
}

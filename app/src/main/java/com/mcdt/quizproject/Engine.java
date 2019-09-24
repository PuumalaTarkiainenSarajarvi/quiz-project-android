package com.mcdt.quizproject;

import org.json.JSONObject;

public class Engine implements RestAsyncTask.OnRequestProgressUpdate
{
    private static Engine m_instance = null;
    private EngineInterface m_callback;

    private Engine() {}

    // static method to create instance of Engine class
    public static Engine getInstance() {
        if (m_instance == null)
            m_instance = new Engine();

        return m_instance;
    }

    public void setCallback(final EngineInterface callback) {
        m_callback = callback;
    }

    public interface EngineInterface
    {

    }

    @Override
    public void requestDone(JSONObject response) {

    }
}

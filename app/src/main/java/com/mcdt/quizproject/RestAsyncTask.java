package com.mcdt.quizproject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class RestAsyncTask
{
    private OnRequestProgressUpdate m_callBack;
    private RequestQueue m_queue;

    interface OnRequestProgressUpdate
    {
        void requestDone(JSONObject response);
    }

    public void setCallBack(OnRequestProgressUpdate callBack) {
        m_callBack = callBack;
    }

    public RestAsyncTask(Context context) {
        m_queue = Volley.newRequestQueue(context);
    }

    public void getRandomQuestion() {
        String requestUrl = Constants.API_BASE_URL + Constants.API_GET_RANDOM_QUESTION;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, requestUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (m_callBack != null) {
                            m_callBack.requestDone(response);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY", "onErrorResponse: " + error);
                    }
                });
        m_queue.add(jsonObjectRequest);
    }
}
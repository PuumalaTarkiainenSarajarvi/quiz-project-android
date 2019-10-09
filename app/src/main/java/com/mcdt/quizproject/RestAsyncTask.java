package com.mcdt.quizproject;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RestAsyncTask
{
    private OnRequestProgressUpdate m_callback;
    private RequestQueue m_queue;

    interface OnRequestProgressUpdate
    {
        void requestDone(final String requestId, JSONObject response);
        void requestDone(final String requestId, JSONArray response);
    }

    public void setCallback(final OnRequestProgressUpdate callback) {
        m_callback = callback;
    }

    public RestAsyncTask(final Context context, final OnRequestProgressUpdate callback) {
        m_queue = Volley.newRequestQueue(context);
        m_callback = callback;
    }

    public void addObjectRequestToQueue(final String baseUrl, final String requestId, final int method) {
        Log.d("VOLLEY", "addRequestToQueue: " + requestId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (method, baseUrl + requestId, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (m_callback != null) {
                            m_callback.requestDone(requestId, response);
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

    public void addObjectRequestToQueue(final String baseUrl, final String requestId, final int method
            , final String sessionId) {
        Log.d("VOLLEY", "addRequestToQueue: " + requestId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (method, baseUrl + requestId, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (m_callback != null) {
                            m_callback.requestDone(requestId, response);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY", "onErrorResponse: " + error);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", sessionId);
                        return headers;
                    }
                };
        m_queue.add(jsonObjectRequest);
    }

    public void addObjectRequestToQueue(final String baseUrl, final String requestId, final int method
            , final String requestBody, final String sessionId) {
        Log.d("VOLLEY", "addRequestToQueue: " + requestId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (method, baseUrl + requestId, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (m_callback != null) {
                            m_callback.requestDone(requestId, response);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY", "onErrorResponse: " + error);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", sessionId);
                        return headers;
                    }
                    @Override
                    public byte[] getBody() {
                        return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
                    }
                };
        m_queue.add(jsonObjectRequest);
    }

    public void addArrayRequestToQueue(final String baseUrl, final String requestId, final int method) {
        Log.d("VOLLEY", "addRequestToQueue: " + requestId);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (method, baseUrl + requestId, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (m_callback != null) {
                            m_callback.requestDone(requestId, response);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY", "onErrorResponse: " + error);
                    }
                });
        m_queue.add(jsonArrayRequest);
    }

    public void addArrayRequestToQueue(final String baseUrl, final String requestId, final int method
            , final String requestBody) {
        Log.d("VOLLEY", "addRequestToQueue: " + requestId);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (method, baseUrl + requestId, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (m_callback != null) {
                            m_callback.requestDone(requestId, response);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY", "onErrorResponse: " + error);
                    }
                }) {
                    @Override
                    public byte[] getBody() {
                        return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
                    }
                };
        m_queue.add(jsonArrayRequest);
    }
}
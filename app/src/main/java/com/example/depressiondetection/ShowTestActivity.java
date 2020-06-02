package com.example.depressiondetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowTestActivity extends AppCompatActivity {
    ProgressBar progressBar;
    ArrayList<TestResult> testResults;
    JSONObject user_data;
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_test);
        progressBar=findViewById(R.id.progressBar);
        testResults=new ArrayList<>();
        try {
            user_data = new JSONObject(PreferenceManager.getDefaultSharedPreferences(this).getString("user_data",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getData();
    }
    public void setData()
    {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        mAdapter = new CustomAdapter(testResults);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }
    public void getData()
    {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId= null;
        try {
            userId = user_data.getString("Id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url="https://us-central1-depression-detection-030498.cloudfunctions.net/api/test?id="+userId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                url,null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if(response.length()==0)
                            Toast.makeText(getApplicationContext(), "No tests present!!", Toast.LENGTH_LONG).show();
                        else {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    testResults.add(new TestResult(
                                            object.getString("Id"),
                                            object.getString("DateTime"),
                                            object.getString("QuestionnaireResult"),
                                            object.getString("ModelResult")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                setData();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.networkResponse.data.toString(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
        });
        queue.add(request);
    }
}

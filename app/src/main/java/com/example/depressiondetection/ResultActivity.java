package com.example.depressiondetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class ResultActivity extends AppCompatActivity {
    TextView result, title;
    Button home;
    String guid;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        result=findViewById(R.id.result);
        title=findViewById(R.id.title);
        home=findViewById(R.id.home);
        result.setText(String.valueOf(getIntent().getIntExtra("depression_value",0)));
        guid= PreferenceManager.getDefaultSharedPreferences(this).getString("GUID","");
        progressBar = findViewById(R.id.progressBar);
        Toast.makeText(this,"Sending data..",Toast.LENGTH_LONG).show();
        makeObjectsInvisible();
        uploadData();
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
    }
    void makeObjectsVisible()
    {
        result.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        home.setVisibility(View.VISIBLE);
    }
    void makeObjectsInvisible()
    {
        result.setVisibility(View.INVISIBLE);
        title.setVisibility(View.INVISIBLE);
        home.setVisibility(View.INVISIBLE);
    }

    void uploadData()
    {

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String url="https://us-central1-depression-detection-030498.cloudfunctions.net/api/test";
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/DepressionDetection/%s/%s.%s";
        String userId=null;
        try {
            userId=new JSONObject(PreferenceManager.getDefaultSharedPreferences(this).getString("user_data","")).getString("Id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            params.put("VIDEO", new File(String.format(path,"VIDEO",guid,"mp4")));
            params.put("JSON", new File(String.format(path,"JSON",guid,"json")));
        }
        catch (FileNotFoundException e) {}
        params.put("ID", userId);
        client.post(url, params,

                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        makeObjectsVisible();
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        makeObjectsVisible();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }

        );

    }

}

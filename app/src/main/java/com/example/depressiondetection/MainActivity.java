package com.example.depressiondetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {
    TextView id,name,dob,phone,gender,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start=findViewById(R.id.start);
        Button allTests=findViewById(R.id.result);
        TextView id=findViewById(R.id.id);
        TextView name=findViewById(R.id.name);
        TextView dob=findViewById(R.id.dateOfBirth);
        TextView phone=findViewById(R.id.phone);
        TextView gender=findViewById(R.id.gender);
        TextView email=findViewById(R.id.email);
        JSONObject user_data;
        try {
            user_data = new JSONObject(PreferenceManager.getDefaultSharedPreferences(this).getString("user_data",""));
            id.setText("User Id : "+user_data.getString("Id"));
            name.setText("Name : "+user_data.getString("Name"));
            dob.setText("DateOfBirth : "+user_data.getString("DateOfBirth"));
            phone.setText("Phone : "+user_data.getString("Phone"));
            gender.setText("Gender : "+user_data.getString("Gender"));
            email.setText("Email : "+user_data.getString("Email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),QuestionnaireActivity.class);
                startActivity(i);
            }
        });
        allTests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),ShowTestActivity.class);
                startActivity(i);
            }
        });
    }
}

package com.example.depressiondetection;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private TextView name,email,phone,dob,pass,pass2;
    private RadioGroup gender;
    Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        phone=findViewById(R.id.phone);
        dob=findViewById(R.id.dob);
        pass=findViewById(R.id.pass);
        pass2=findViewById(R.id.pass2);
        gender=findViewById(R.id.gender);
        register=findViewById(R.id.register);
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calender = Calendar.getInstance();
                int day = calender.get(Calendar.DAY_OF_MONTH);
                int month = calender.get(Calendar.MONTH);
                int year = calender.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dob.setText(String.format("%d/%d/%d", dayOfMonth, monthOfYear + 1, year));
                            }
                        }, year, month, day);
                picker.show();
            }});
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(validateUser())
                    {
                        registerUser();
                    }
                    else
                    {
                     /*   new AlertDialog.Builder(getApplicationContext())
                                .setTitle("Error!!")
                                .setMessage("Please fill in the values properly!!")
                                .setPositiveButton("Yes", null).show();*/
                    }
                }
            });
    }
    private void registerUser(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url="https://us-central1-depression-detection-030498.cloudfunctions.net/api/register";
        JSONObject requestPayload = new JSONObject();
        try{
            requestPayload.put("Name",name.getText().toString());
            requestPayload.put("Email",email.getText().toString());
            requestPayload.put("Phone",phone.getText().toString());
            requestPayload.put("DateOfBirth",dob.getText().toString());
            requestPayload.put("Gender",findViewById(gender.getCheckedRadioButtonId()).toString());
            requestPayload.put("Password",pass.getText().toString());

            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, requestPayload,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSONPost", response.toString());
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                                .putString("user_data",response.toString()).apply();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("JSONPost", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.networkResponse.data.toString(), Toast.LENGTH_SHORT).show();

                //pDialog.hide();
            }
        });
        queue.add(request);
    }
    private boolean validateUser(){
        if(!(validateUserName()&&validateEmail()&&validateDob()&&validateGender()&&validatePhone()&&validatePass()))
            return false;
        else
            return  true;
    }
    private boolean validateUserName()
    {
        if(name.getText().toString().isEmpty())
        {
            name.setError("Please enter your name");
            return false;
        }
        else
        {
            name.setError(null);
            return  true;
        }
    }
    private boolean validateEmail()
    {
        if(email.getText().toString().isEmpty())
        {
            email.setError("Please enter your email");
            return false;
        }
        else
        {
            email.setError(null);
            return  true;
        }
    }
    private boolean validatePhone()
    {
        if(phone.getText().toString().isEmpty())
        {
            phone.setError("Please enter your phone");
            return false;
        }
        else
        {
            phone.setError(null);
            return  true;
        }
    }
    private boolean validateDob()
    {
        if(dob.getText().toString().isEmpty())
        {
            name.setError("Please enter your dob");
            return false;
        }
        else
        {
            name.setError(null);
            return  true;
        }
    }
    private boolean validatePass()
    {
        String p1=pass.getText().toString();
        String p2=pass2.getText().toString();
        if(p1.isEmpty())
        {
            pass.setError("Please enter password");
            return false;
        }
        if(p2.isEmpty())
        {
            pass2.setError("Please renter password");
            return false;
        }
        else if(!p1.equals(p2))
        {
            pass2.setError("Passwords don't match");
            return false;
        }
        else
        {
            pass.setError(null);
            pass2.setError(null);
            return true;
        }
    }
    private boolean validateGender()
    {
        RadioButton lastButton=((RadioButton)gender.getChildAt(1));
        if(gender.getCheckedRadioButtonId()==-1)
        {
            lastButton.setError("Your error");
            return false;
        }
        else
        {
            lastButton.setError(null);
            return true;
        }
    }
}

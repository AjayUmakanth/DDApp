package com.example.depressiondetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class QuestionnaireActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "Recorder";
    public static SurfaceView mSurfaceView;
    public static SurfaceHolder mSurfaceHolder;
    public static Camera mCamera ;
    public static boolean mPreviewRunning;
    String guid;
    JSONObject user_data;
    int score;
    private String[] questions;
    private int currentQuestion;
    private TextView question;
    private RadioGroup radioGroup;
    private int[] answers;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    File storagePath;
    public MediaRecorder mrec = new MediaRecorder();
    private Button startRecording = null;
    String neroskyValues[]={"AlphaMin","AlphaMax","AlphaAvg",
                            "BetaMin","BetaMax","BetaAvg",
                            "GammaMin","GammaMax","GammaAvg",
                            "ThetaMin","ThetaMax","ThetaAvg",
                            "DeltaMin","DeltaMax","DeltaAvg"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        Button next=findViewById(R.id.next);
        guid= UUID.randomUUID().toString();
        Button previous=findViewById(R.id.previous);
        try {
            user_data = new JSONObject(PreferenceManager.getDefaultSharedPreferences(this).getString("user_data",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        storagePath = new File(Environment.getExternalStorageDirectory() + "/DepressionDetection/Video");
        if(!storagePath.exists()) storagePath.mkdirs();
        storagePath = new File(Environment.getExternalStorageDirectory() + "/DepressionDetection/Json");
        if(!storagePath.exists()) storagePath.mkdirs();

        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        currentQuestion=0;
        question=findViewById(R.id.question);
        answers=new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1};

        questions=new String[]{"Little interest or pleasure in doing things",
            "Feeling down, depressed, or hopeless",
            "Trouble falling or staying asleep, or sleeping too much",
            "Feeling tired or having little energy",
            "Poor appetite or overeating",
            "Feeling bad about yourself or that you are a failure or have let yourself or your family down",
            "Trouble concentrating on things, such as reading the newspaper or watching television",
            "Moving or speaking so slowly that other people could have noticed. Or the opposite being so fidgety or restless that you have been moving around a lot more than usual",
            "Thoughts that you would be better off dead, or of hurting yourself"};
        radioGroup = findViewById(R.id.radioGroup);
        setQnA();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {nextQuestion();}});
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {previousQuestion();}});

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                .putString("GUID",guid).apply();

        Intent intent = new Intent(QuestionnaireActivity.this, RecorderService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);

    }

    @Override
    public void onBackPressed() {
        previousQuestion();
    }

    public int getAge(String userDob) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(Integer.valueOf(userDob.substring(0,4)), Integer.valueOf(userDob.substring(5,7))-1, Integer.valueOf(userDob.substring(8)));

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    private  void getNeuroskyValues()
    {
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        Random rand = new Random();
        JSONObject test_data = new JSONObject();
        try {
            test_data.put("UserId",user_data.getString("Id"));
            test_data.put("DateTime",dateTime);
            test_data.put("Gender",user_data.getString("Gender"));
            test_data.put("Age",getAge(user_data.getString("DateOfBirth")));
            test_data.put("QuestionnaireResult",score);
            for (String entry:neroskyValues) {
                test_data.put(entry,rand.nextFloat());
            }
            try {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/DepressionDetection/Json/" + guid + ".json");
                Writer writer = new BufferedWriter(new FileWriter(file));
                writer.write(test_data.toString());
                writer.close();
            } catch (Exception e) {
                Log.e("File Exception: ", e.getMessage());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void nextQuestion()
    {
        if (storeAnswer())
        {
            currentQuestion++;
            if (currentQuestion < 9)
                setQnA();
            else {
                stopService(new Intent(QuestionnaireActivity.this, RecorderService.class));
                getScore();
                getNeuroskyValues();
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("depression_value",score);
                startActivity(intent);
            }
        }
        else
            Toast.makeText(this, "Please select an option!!", Toast.LENGTH_SHORT).show();
    }

    private void previousQuestion()
    {
        if(currentQuestion==0)
        {
            stopService(new Intent(QuestionnaireActivity.this, RecorderService.class));
            finish();
        }
        currentQuestion--;
        setQnA();
    }

    private Boolean storeAnswer()
    {
        int idx = radioGroup.getCheckedRadioButtonId();
        if(idx==-1)
            return false;
        else
        {
            answers[currentQuestion] = radioGroup.indexOfChild(radioGroup.findViewById(idx));
            return true;
        }
    }

    private void setQnA()
    {
        question.setText(questions[currentQuestion]);
        question.setGravity(Gravity.CENTER_HORIZONTAL);
        question.setPadding(5,25,5,25);

        if(answers[currentQuestion]==-1)
            radioGroup.clearCheck();
        else
            radioGroup.check(radioGroup.getChildAt(answers[currentQuestion]).getId());
    }

    private void getScore()
    {
        int sum=0;
        for (int x: answers)
            sum+=x;
        score=sum;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void uploadData(String url) {
    }

}

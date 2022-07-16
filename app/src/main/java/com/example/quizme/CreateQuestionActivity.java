package com.example.quizme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.quizme.utility.NetworkChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateQuestionActivity extends AppCompatActivity {

//    ImageView questionImage;
//    Button pickImage;
    Button addQuestion;
    Button viewQuestions;
    Button uploadQuiz;
    Button questionCount;
//    Button deleteImage;
    EditText questionTitle;
    EditText ans1;
    EditText ans2;
    EditText ans3;
    EditText ans4;
//    Uri imageUri;
    RadioGroup selectAnswerSection;
    int correctAnswer = -1;
    TextView showCorrectAnswer;
    LoadingDialog loadDialog;

    private static final int PICK_IMAGE = 100;
    private  static  final  int STORAGE_PERMISSION_CODE = 1001;
    private static final int IMAGE_PICK_CODE = 1000;

    private Bitmap bitmap;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);

        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);

        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

       // requestStoragePermission();

//        questionImage = findViewById(R.id.quesImg);
//        pickImage = findViewById(R.id.pickImage);
        viewQuestions = findViewById(R.id.viewList);
        uploadQuiz = findViewById(R.id.submitQuiz);
        addQuestion = findViewById(R.id.addQuestion);
        questionTitle = findViewById(R.id.qId);
        questionCount = findViewById(R.id.questionCount);
//        deleteImage = findViewById(R.id.deleteImage);
        ans1 = findViewById(R.id.qA1);
        ans2 = findViewById(R.id.qA2);
        ans3 = findViewById(R.id.qA3);
        ans4 = findViewById(R.id.qA4);
        selectAnswerSection = findViewById(R.id.correctAnswerRadioGroup);
        showCorrectAnswer = findViewById(R.id.text_correct_answer);


        questionCount.setText("Question Count : " + GlobalData.getLength());

        if(GlobalData.getModifiedQuestion() != null){

            Question tmpQuestion = GlobalData.getModifiedQuestion();
            GlobalData.setModifiedQuestion(null);

            questionTitle.setText(tmpQuestion.getQuestion());
//            questionImage.setImageURI(tmpQuestion.getImageUri());
            ans1.setText(tmpQuestion.getAnswer1());
            ans2.setText(tmpQuestion.getAnswer2());
            ans3.setText(tmpQuestion.getAnswer3());
            ans4.setText(tmpQuestion.getAnswer4());
//            imageUri = tmpQuestion.getImageUri();
            correctAnswer = tmpQuestion.getCorrectAnswer();
            selectAnswerSection.check((selectAnswerSection.getChildAt(correctAnswer-1)).getId());
            String correct = "Correct Answer = "+((correctAnswer == -1)?"Not Selected":correctAnswer);
            showCorrectAnswer.setText(correct);
        }

//        pickImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
//                        //permission not granted.request it
//                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                        //show popup for runtime permission
//                        requestPermissions(permissions, STORAGE_PERMISSION_CODE);
//                    }
//                    else{
//                        //permission already granted
//                        openGallery();
//                    }
//                }
//                else {
//                    //system os is less than marshmallow
//                    openGallery();
//                }
//            }
//        });

        viewQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(GlobalData.getLength()>0) {
                    Intent intent = new Intent(CreateQuestionActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"No Questions to VIew",Toast.LENGTH_SHORT);
                }
            }
        });

        uploadQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(GlobalData.getLength() == 0) {
                    Toast.makeText(CreateQuestionActivity.this,"No Questions to Upload",Toast.LENGTH_SHORT).show();
                }
                else{
                    loadDialog = new LoadingDialog(CreateQuestionActivity.this);
                    loadDialog.startLoadingDialog();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            doPostRequest();
                        }
                    }).start();
                }

            }
        });

//        deleteImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                questionImage.setImageURI(null);
//                imageUri = null;
//
//            }
//        });

        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Question tmpQuestion ;
                String title = questionTitle.getText().toString();
                String answer1 = ans1.getText().toString();
                String answer2 = ans2.getText().toString();
                String answer3 = ans3.getText().toString();
                String answer4 = ans4.getText().toString();



                Log.e("ques",questionTitle.getText().toString());

                if(title.trim().length() == 0){
                    //questionTitle.requestFocus();
                    questionTitle.setError("Question name can not be empty");
                }
                if(answer1.trim().length() == 0){
                    //ans1.requestFocus();
                    ans1.setError("Answer1 can not be empty");
                }
                if(answer2.trim().length() == 0){
                    //ans2.requestFocus();
                    ans2.setError("Answer2 can not be empty");
                }
                if(answer3.trim().length() == 0){
                    //ans3.requestFocus();
                    ans3.setError("Answer3 can not be empty");
                }
                if(answer4.trim().length() == 0){
                   // ans4.requestFocus();
                    ans4.setError("Answer4 can not be empty");
                }
                if(correctAnswer == -1) {
                    showCorrectAnswer.requestFocus();
                    showCorrectAnswer.setError("Select the Correct Answer");
                }

                if(title.trim().length() != 0 && answer1.trim().length() != 0 && answer2.trim().length() != 0 && answer3.trim().length() != 0 && answer4.trim().length() != 0 && correctAnswer != -1) {

//                    if (imageUri != null) {
//                        tmpQuestion = new Question(title, GlobalData.getLength(), answer1, answer2, answer3, answer4, imageUri,correctAnswer);
//
//                    } else {
//                        tmpQuestion = new Question(title, GlobalData.getLength(), answer1, answer2, answer3, answer4,correctAnswer);
//                    }
                    tmpQuestion = new Question(title, GlobalData.getLength(), answer1, answer2, answer3, answer4,correctAnswer);

                    GlobalData.addQuestion(tmpQuestion);
                    questionCount.setText("Question Count : "+GlobalData.getLength());
//                    questionImage.setImageURI(null);
//                    imageUri = null;
                    questionTitle.setText("");
                    ans1.setText("");
                    ans2.setText("");
                    ans3.setText("");
                    ans4.setText("");
                    correctAnswer = -1;
                    String correct = "Correct Answer = "+((correctAnswer == -1)?"Not Selected":correctAnswer);
                    showCorrectAnswer.setText(correct);
                    selectAnswerSection.clearCheck();
                   // Log.e("sample question", GlobalData.getQuestion(0).getImageUri().toString());


                }

            }
        });
    }



    private void doPostRequest() {
        Log.d("Okhttp3:", "doPostRequest function called");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String baseURL =pref.getString("baseURL",null);
        String url = baseURL + "/quiz/add";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // connect timeout
                .writeTimeout(30, TimeUnit.SECONDS) // write timeout
                .readTimeout(30, TimeUnit.SECONDS) // read timeout
                .build();

        MediaType JSON = MediaType.parse("application/json;charset=utf-8");

        JSONArray questionsArray = new JSONArray();
        for (Question question : GlobalData.getProblems()) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("question", question.getQuestion());
                JSONArray ja = new JSONArray(question.getAnswers());
                jo.put("answers", ja);
                jo.put("correctAnswer", question.getCorrectAnswer());


            } catch (JSONException e) {
                e.printStackTrace();
            }
            questionsArray.put(jo);
        }

        JSONObject actualData = new JSONObject();

        try {
            actualData.put("name", GlobalData.getName());
            actualData.put("link", GlobalData.getLink());
            actualData.put("startDate", GlobalData.getStartDate());
            actualData.put("startTime", GlobalData.getStartTime());
            actualData.put("duration", GlobalData.getDuration());
            actualData.put("noOfProblems", Math.max(1, GlobalData.getNoOfProblems()));
            actualData.put("problems", questionsArray);
        } catch (JSONException e) {
            Log.d("Okhttp3:", "JSON Exception");
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(JSON, actualData.toString());
        Log.d("Okhttp3:", "Requestbody created");
        Log.d("Okhttp3:", "body = \n" + body.toString());
        Log.d("Okhttp3:", "actualData = \n" + actualData.toString());
        String jwt=pref.getString("jwt",null);
        Log.d("Okhttp3:", "jwt = "+jwt);
        Request request = new Request.Builder()
                .header("Authorization", "Bearer "+jwt)
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            Log.d("Okhttp3:", "request done, got the response");
            Log.d("Okhttp3:", response.body().string());


            final String toast_message;

            if(response.code() == 200){

                PopUpSubmission.quiz_link = GlobalData.getLink();
                PopUpSubmission.name = GlobalData.getName();
                PopUpSubmission.startDate = GlobalData.getStartDate();
                PopUpSubmission.startTime = GlobalData.getStartTime();
                PopUpSubmission.duration = GlobalData.getDuration();
                loadDialog.dismissDialog();
                startActivity(new Intent(CreateQuestionActivity.this,PopUpSubmission.class));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clearQuizData();
                    }
                });

            }
            else {
                toast_message = "Something Went Wrong";
                if (getApplicationContext() != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), toast_message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }



        } catch (IOException e) {
            Log.d("Okhttp3:", "IOEXCEPTION while request");
            e.printStackTrace();
        }
    }

    private void clearQuizData() {

        //clear Quiz data
        GlobalData.clear();

        questionTitle.setText("");
        ans1.setText("");
        ans2.setText("");
        ans3.setText("");
        ans4.setText("");
        questionCount.setText("Question Count : "+GlobalData.getLength());
        QuizDetailsActivity.clearTexts();

        correctAnswer = -1;
        String correct = "Correct Answer = "+((correctAnswer == -1)?"Not Selected":correctAnswer);
        showCorrectAnswer.setText(correct);
        selectAnswerSection.clearCheck();

        Toast.makeText(getApplicationContext(),"All Cleared",Toast.LENGTH_SHORT);
        //TODO : clear image data
    }

    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE && data.getData() != null){
//            imageUri = data.getData();
//            questionImage.setImageURI( imageUri);
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
//                questionImage.setImageBitmap(bitmap);
//            }
//            catch (IOException e){
//                e.printStackTrace();
//            }
        }
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void answerCheckButton(View view){
        int radioId = selectAnswerSection.getCheckedRadioButtonId();

        View radioButton = selectAnswerSection.findViewById(radioId);
        correctAnswer = selectAnswerSection.indexOfChild(radioButton)+1;

//        Toast.makeText(this,"Selected Radio Button = "+correctAnswer,Toast.LENGTH_SHORT).show();
        String correct = "Correct Answer = "+((correctAnswer == -1)?"Not Selected":correctAnswer);
        showCorrectAnswer.setText(correct);
        showCorrectAnswer.setError(null);
    }

}

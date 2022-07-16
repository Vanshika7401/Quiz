package com.example.quizme;


import android.net.Uri;

import java.util.ArrayList;

public class EditQuestion {

    private String question;
    private int questionNum;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    int providedAns;
    private int correctAns;
    private String imageUri;

    //use in both past and edit questions
    public EditQuestion(String question, int questionNum, String answer1, String answer2, String answer3, String answer4, int correctAns, String imageUri) {
        this.question = question;
        this.questionNum = questionNum;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.correctAns = correctAns;
        this.imageUri = imageUri;
    }

    public EditQuestion(String question,int questionNum, String answer1, String answer2, String answer3, String answer4, int correctAns, String imageUri,int ans) {
        this.question = question;
        this.questionNum = questionNum;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.correctAns = correctAns;
        this.imageUri = imageUri;
        this.providedAns = ans;
    }

    public int getProvidedAns() {
        return providedAns;
    }

    public void setProvidedAns(int providedAns) {
        this.providedAns = providedAns;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(int questionNum) {
        this.questionNum = questionNum;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public int getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(int correctAns) {
        this.correctAns = correctAns;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
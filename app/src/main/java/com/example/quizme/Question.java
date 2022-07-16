package com.example.quizme;

import android.net.Uri;

import java.util.ArrayList;

public class Question {

    private String question;
    private int questionNum;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private int correctAns;
    private int clientAns;
    private Uri imageUri;
    private ArrayList<String> answers = new ArrayList<>();

    public Question(String question, int questionNum, String answer1, String answer2, String answer3, String answer4, Uri imageUri,int correctAns) {
        this.question = question;
        this.questionNum = questionNum;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.imageUri = imageUri;
        this.correctAns = correctAns;
        setAnswerList();
    }

    public Question(String question, int questionNum, String answer1, String answer2, String answer3, String answer4,int correctAns) {
        this.question = question;
        this.questionNum = questionNum;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.correctAns = correctAns;
        setAnswerList();
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
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
        setAnswerList();
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
        setAnswerList();
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
        setAnswerList();
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
        setAnswerList();
    }

    public int getCorrectAnswer() {
        return correctAns;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAns = correctAnswer;
    }

    public void setAnswerList(){
        this.answers.clear();
        this.answers.add(this.answer1);
        this.answers.add(this.answer2);
        this.answers.add(this.answer3);
        this.answers.add(this.answer4);
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public int getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(int correctAns) {
        this.correctAns = correctAns;
    }

    public int getClientAns() {
        return clientAns;
    }

    public void setClientAns(int clientAns) {
        this.clientAns = clientAns;
    }

    public boolean checkAnswer(){
        if(this.correctAns == this.clientAns) return true;
        return false;
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", questionNum=" + questionNum +
                ", answer1='" + answer1 + '\'' +
                ", answer2='" + answer2 + '\'' +
                ", answer3='" + answer3 + '\'' +
                ", answer4='" + answer4 + '\'' +
                ", correctAns=" + correctAns +
                ", clientAns=" + clientAns +
                ", answers=" + answers +
                '}';
    }
}

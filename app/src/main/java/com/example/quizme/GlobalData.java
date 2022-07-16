package com.example.quizme;

import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.Date;

public class GlobalData {

    private static String name;//quiz name
    private static String link;//quiz link
    private static String startDate;//yyyy-MM-dd format
    private static String startTime;//HH:mm format
    private static String duration;//in minutes
    private static ArrayList<QuizResult> markList = new ArrayList<>();
    private static int noOfProblems;
    private static ArrayList<Question> problems = new ArrayList<>();
    private static Question modifiedQuestion;
    public static ArrayList<Question> clientQuestions = new ArrayList<>();
    private static LeaderBoard[] leaderBoards;
    private static String QuizId;
    private static Date endTime;
    private static int quizStatus;
    private static int quizDuration;
    private static CountDownTimer countDownTimer = null;


    public static Question getModifiedQuestion() {
        return modifiedQuestion;
    }

    public static void setModifiedQuestion(Question modifiedQuestion) {
        GlobalData.modifiedQuestion = modifiedQuestion;
    }

    public static ArrayList<Question> getProblems() {
        return problems;
    }

    public static void setProblems(ArrayList<Question> problems) {
        GlobalData.problems = problems;
        setNoOfProblems(problems.size());
    }

    public static void addQuestion(Question question){

        problems.add(question);

    }

    public static Question getQuestion(int index){
        return problems.get(index);
    }

    public static void deleteQuestion(int index){

        problems.remove(index);

    }

    public static void modifyQuestion(int index,Question question){

        problems.set(index,question);

    }

    public static void modifyClientQuestion(int index,Question question){

        clientQuestions.set(index,question);

    }

    public static int getLength(){
        return problems.size();
    }

    public  static  int getLengthClient(){
        return  clientQuestions.size();
    }

    public static void reduceIndex(int index){
        Question tmpQuestion;

        for(int i = index; i< problems.size(); i++){

            tmpQuestion = problems.get(i);
            tmpQuestion.setQuestionNum(i);
            problems.set(i,tmpQuestion);
        }
    }

    public static String getName() {
        return name;
    }

    public static void setName(String input_name) {
        name = input_name;
    }

    public static String getLink() {
        return link;
    }

    public static void setLink(String input_link) {
        link = input_link;
    }

    public static String getStartDate() {
        return startDate;
    }

    public static void setStartDate(String input_startDate) {
        startDate = input_startDate;
    }

    public static String getStartTime() {
        return startTime;
    }

    public static void setStartTime(String input_startTime) {
        startTime = input_startTime;
    }

    public static String getDuration() {
        return duration;
    }

    public static void setDuration(String input_duration) {
        duration = input_duration;
    }

    public static int getNoOfProblems() {
        return noOfProblems;
    }

    public static void setNoOfProblems(int no) {
        noOfProblems = no;
    }

    public static void clear(){
        name = "";
        link = "";
        startDate = "";
        startTime = "";
        duration = "";
        noOfProblems = 0;
        problems = new ArrayList<>();
        modifiedQuestion = null;
    }
    public static void addClientQuestion(Question question){
        clientQuestions.add(question);
    }

    public static ArrayList<Question> getClientQuestions(){
        return clientQuestions;
    }

    public static void removeAllClientQuestions(){
        clientQuestions.clear();
    }

    public  static int getMarks(){
        int count =0;

        for(int i = 0;i<clientQuestions.size();i++){

            Question tmp = clientQuestions.get(i);
            if(tmp.getClientAns() == tmp.getCorrectAns()) count++;

        }
        return count;
    }

    public static ArrayList<QuizResult> getMarkList() {
        return markList;
    }

    public static LeaderBoard[] getLeaderBoards() {
        return leaderBoards;
    }

    public static void setLeaderBoards(LeaderBoard[] leaderBoards) {

        GlobalData.leaderBoards = new LeaderBoard[leaderBoards.length];
        GlobalData.leaderBoards = leaderBoards;
    }

    public static String getLeaderBoardName(int index){
        return leaderBoards[index].getQuizName();

    }

    public static int getLeaderBoardLength(){
        return leaderBoards.length;
    }

    public static LeaderBoard getLeaderBoard(int index){
        return leaderBoards[index];
    }

    public static String getQuizId() {
        return QuizId;
    }

    public static void setQuizId(String quizId) {
        QuizId = quizId;
    }

    public static Date getEndTime() {
        return endTime;
    }

    public static void setEndTime(Date endTime) {
        GlobalData.endTime = endTime;
    }

    public static int getQuizStatus() {
        return quizStatus;
    }

    public static void setQuizStatus(int quizStatus) {
        GlobalData.quizStatus = quizStatus;
    }

    public static int getQuizDuration() {
        return quizDuration;
    }

    public static void setQuizDuration(int quizDuration) {
        GlobalData.quizDuration = quizDuration;
    }

    public static CountDownTimer getCountDownTimer() {
        return countDownTimer;
    }

    public static void setCountDownTimer(CountDownTimer countDownTimer) {
        GlobalData.countDownTimer = countDownTimer;
    }
    public static void stopTimer(){
        if(countDownTimer != null) countDownTimer.cancel();
    }
}

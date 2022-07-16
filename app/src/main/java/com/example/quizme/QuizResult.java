package com.example.quizme;

public class QuizResult {

    private String participantName;
    private float marks;

    public QuizResult(String participantName, float marks) {
        this.participantName = participantName;
        this.marks = marks;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public float getMarks() {
        return marks;
    }

    public void setMarks(float marks) {
        this.marks = marks;
    }
}

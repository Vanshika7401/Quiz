package com.example.quizme;

public class SingleResult {

        private String user;
        private float marks;

        public SingleResult(String user, float marks) {
            this.user = user;
            this.marks = marks;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public float getMarks() {
            return marks;
        }

        public void setMarks(float marks) {
            this.marks = marks;
        }


}

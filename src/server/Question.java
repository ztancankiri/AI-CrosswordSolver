package server;

public class Question {

    public static final int DOWN = 0;
    public static final int ACROSS = 1;

    public String question;
    public int score;
    public int type;

    public Question(String question, int score, int type) {
        this.question = question;
        this.score = score;
        this.type = type;
    }

    public boolean isDown() {
        return type == DOWN;
    }

    public boolean isAcross() {
        return type == ACROSS;
    }
}

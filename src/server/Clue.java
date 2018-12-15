package server;

import java.util.ArrayList;

public class Clue {

    public static final int DOWN = 0;
    public static final int ACROSS = 1;

    public String question;
    public int score;
    public int type;
    public int no;


    public boolean isPlural;
    public ArrayList<Word> words;
    public PyExecutor executor;

    public Clue(String question, int score, int type, int no) {
        this.question = question;
        this.score = score;
        this.type = type;
        this.no = no;
        executor = new PyExecutor("python3", "isPlural.py");
        try{
            isPlural = Boolean.parseBoolean(executor.exec(question));
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public boolean isDown() {
        return type == DOWN;
    }

    public boolean isAcross() {
        return type == ACROSS;
    }

    public String toString(){
        String returnString = "";
        returnString += (question + " " + score + " " + type + " " + no + " " + isPlural);
        return returnString;
    }
}
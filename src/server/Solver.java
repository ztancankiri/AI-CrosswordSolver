package server;

import java.util.ArrayList;

public class Solver {
    ArrayList<Clue> clues;
    PyExecutor antonymEx;
    PyExecutor pluralizeEx;
    PyExecutor singularizeEx;
    PyExecutor synonymEx;

    public Solver(ArrayList<Clue> clues){
        this.clues = new ArrayList<>();
        for(Clue clue: clues){
            this.clues.add(clue);
        }
    }

    public void solve(){

    }

    public String toString(){
        String str = "";
        for(Clue clue: clues){
            str += clue.toString() + "\n";
        }
        return str;
    }
}

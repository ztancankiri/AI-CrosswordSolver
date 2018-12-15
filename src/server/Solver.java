package server;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Solver {

    private ArrayList<Clue> clues;

    private char[][] grid;

    private PyExecutor antonymEx;
    private PyExecutor pluralizeEx;
    private PyExecutor singularizeEx;
    private PyExecutor synonymEx;

    public Solver() {
        this.clues = new ArrayList<>();
        this.grid = new char[5][5];
    }

    public void solve(){
        System.out.println("Clue Length");
        calculateClueLength();

        ArrayList<ClueCandidateSearcher> crawlers = new ArrayList<>();
        for (int i = 0; i < clues.size(); i++) {
            ClueCandidateSearcher clueCandidateSearcher = new ClueCandidateSearcher(clues.get(i));
            crawlers.add(clueCandidateSearcher);
            clueCandidateSearcher.start();
        }

        for (ClueCandidateSearcher clueCandidateSearcher : crawlers) {
            try {
                clueCandidateSearcher.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (ClueCandidateSearcher clueCandidateSearcher : crawlers) {
            clueCandidateSearcher.getClue().candidates = clueCandidateSearcher.getCollection();
        }

        System.out.println("Crawling finished.");

        for (int i = 0; i < clues.size(); i++) {
            ArrayList<Word> temp = clues.get(i).candidates;

            CandidateFilter filter1 = new CandidateFilter(temp);
            ArrayList<Word> filtered1 = filter1.filterByLength(clues.get(i).answerLength);

            CandidateFilter filter2 = new CandidateFilter(filtered1);
            ArrayList<Word> filtered2 = filter2.cleanReplicates();

            CandidateFilter filter3 = new CandidateFilter(filtered2);
            ArrayList<Word> filtered3 = filter3.removeStopwords();

            clues.get(i).candidates = filtered3;

            try {
                FileWriter fileWriter = new FileWriter(clues.get(i).no + "-" + clues.get(i).type + ".txt");
                PrintWriter printWriter = new PrintWriter(fileWriter);

                System.out.println(clues.get(i).question);
                printWriter.println(clues.get(i).question);

                System.out.println();
                printWriter.println();

                for (Word w : clues.get(i).candidates) {
                    System.out.println(w.word + " (" + w.freq + ") -> " + w.source);
                    printWriter.println(w.word + " (" + w.freq + ") -> " + w.source);
                }
                printWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addClue(Clue clue) {
        clues.add(clue);
    }

    public void addQPos(int no, int pos) {
        for (int i = 0; i < clues.size(); i++) {
            if (clues.get(i).no == no) {
                clues.get(i).pos = pos;
            }
        }
    }

    public void addBlackCellsToGrid(int pos) {
        int col = (pos - 1) % 5;
        int row = (pos - 1) / 5;

        grid[row][col] = '#';
    }

    public void calculateClueLength() {
        for (int i = 0; i < clues.size(); i++) {
            int counter = 0;

            if (clues.get(i).isAcross()) {
                int col = (clues.get(i).pos - 1) % 5;
                int row = (clues.get(i).pos - 1) / 5;

                for (int j = col; j < 5; j++) {
                    if (grid[row][j] != '#')
                        counter++;
                    else
                        j = 5; // break
                }
            }
            else if (clues.get(i).isDown()) {
                int col = (clues.get(i).pos - 1) % 5;
                int row = (clues.get(i).pos - 1) / 5;

                for (int j = row; j < 5; j++) {
                    if (grid[j][col] != '#')
                        counter++;
                    else
                        j = 5; // break
                }
            }
            clues.get(i).answerLength = counter;
        }
    }

    public String toString(){
        String str = "";
        for(Clue clue: clues){
            str += clue.toString() + "\n";
        }
        return str;
    }

    public void reset() {
        this.clues = new ArrayList<>();
        this.grid = new char[5][5];

    }
}

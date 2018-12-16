package server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
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

        this.antonymEx = new PyExecutor("python3", "antonyms.py");
        this.synonymEx = new PyExecutor("python3", "synonyms.py");
        this.pluralizeEx = new PyExecutor("python3", "pluralize.py");
        this.singularizeEx = new PyExecutor("python3", "singularize.py");
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

            CandidateFilter stopCleaner = new CandidateFilter(temp);
            ArrayList<Word> stopCleaned = stopCleaner.removeStopwords();

            /*System.out.println("Going to py scripts...");

            if (clues.get(i).isPlural) {
                System.out.println(clues.get(i).isPlural);
                try {
                    JSONArray pluralizedArr = pluralizeEx.exec(stopCleaned);
                    System.out.println(pluralizedArr);

                    for (Object obj : pluralizedArr) {
                        JSONObject object = (JSONObject) obj;
                        String word = object.getString("word");
                        String plural = object.getString("plural");

                        for (Word w : stopCleaned) {
                            if (w.word.equals(word))
                                w.word = plural;
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println(clues.get(i).isPlural);
                try {
                    JSONArray singularizedArr = singularizeEx.exec(stopCleaned);
                    System.out.println(singularizedArr);

                    for (Object obj : singularizedArr) {
                        JSONObject object = (JSONObject) obj;
                        String word = object.getString("word");
                        String singular = object.getString("singular");

                        for (Word w : stopCleaned) {
                            if (w.word.equals(word))
                                w.word = singular;
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }*/

            CandidateFilter repCleaner = new CandidateFilter(stopCleaned);
            ArrayList<Word> repCleaned = repCleaner.cleanReplicates();

            CandidateFilter lengthFilter = new CandidateFilter(repCleaned);
            ArrayList<Word> lengthFiltered = lengthFilter.filterByLength(clues.get(i).answerLength);

            CandidateFilter clueWordFilter = new CandidateFilter(lengthFiltered);
            ArrayList<Word> clueWordFiltered = clueWordFilter.removeClueWords(clues.get(i).question);

            CandidateFilter filter = new CandidateFilter(clueWordFiltered);
            ArrayList<Word> filterFiltered = filter.removeClueWords(clues.get(i).question);

            clues.get(i).candidates = filterFiltered;

            try {
                FileWriter fileWriter = new FileWriter("candidateLists/" + clues.get(i).no + "-" + clues.get(i).type + ".txt");
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

            System.out.println();
            System.out.println("Candidate Lists are populated.");
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

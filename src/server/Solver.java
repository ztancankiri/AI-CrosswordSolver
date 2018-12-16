package server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Solver {

    private ArrayList<Clue> clues;

    private String[][] grid;

    private PyExecutor antonymEx;
    private PyExecutor pluralizeEx;
    private PyExecutor singularizeEx;
    private PyExecutor synonymEx;

    public Solver() {
        this.clues = new ArrayList<>();
        this.grid = new String[5][5];

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                grid[i][j] = "0";

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


            System.out.println();
            System.out.println("Puzzle solving...");
        }
        solvePuzzle();
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

        grid[row][col] = "#";
    }

    public void calculateClueLength() {
        for (int i = 0; i < clues.size(); i++) {
            int counter = 0;

            if (clues.get(i).isAcross()) {
                int col = (clues.get(i).pos - 1) % 5;
                int row = (clues.get(i).pos - 1) / 5;

                for (int j = col; j < 5; j++) {
                    if (!grid[row][j].equals("#"))
                        counter++;
                    else
                        j = 5; // break
                }
            }
            else if (clues.get(i).isDown()) {
                int col = (clues.get(i).pos - 1) % 5;
                int row = (clues.get(i).pos - 1) / 5;

                for (int j = row; j < 5; j++) {
                    if (!grid[j][col].equals("#"))
                        counter++;
                    else
                        j = 5; // break
                }
            }
            clues.get(i).answerLength = counter;
        }
    }

    public void reset() {
        this.clues = new ArrayList<>();
        this.grid = new String[5][5];

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                grid[i][j] = "0";
    }

    ArrayList<Clue> testCase;

    public void solvePuzzle() {
        calculateAVGandSTD();

        testCase = new ArrayList<>();

        for (Clue clue : clues)
            testCase.add(clue);

        for (Clue clue : testCase) {
            ArrayList<Word> temp = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                temp.add(clue.candidates.get(i));
            }
            clue.candidates = temp;
        }

        System.out.println("going to recursion...");
        recursion(testCase);

        printGrid();
    }

    private void printGrid() {
        System.out.println();
        System.out.println("---GRID---");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                System.out.print(grid[i][j] + "\t");

            System.out.println();
        }
    }

    private void recursion(ArrayList<Clue> possibleClues) {
        printGrid();
        Clue maxClue = findMaxClue(possibleClues);
        putClueToGrid(maxClue);
    }

    private void calculateAVGandSTD() {
        int sum;
        for(Clue clue : clues){
            sum = 0;
            for(int i = 0; i < 100; i++){
                sum += clue.candidates.get(i).freq;
            }
            clue.avg = sum / 100;
            for(int i = 0; i < 100; i++){
                clue.std = clue.std + Math.pow(clue.candidates.get(i).freq - clue.avg, 2);
            }
        }
    }

    private Clue findMaxClue(ArrayList<Clue> possibleClues) {
        /*Clue maxClue = possibleClues.get(0);

        for(Clue clue : possibleClues){
            if(clue.std > maxClue.std)
                maxClue = clue;
        }

        return maxClue;*/

        return possibleClues.get(0);
    }

    private void putClueToGrid(Clue clue) {
        int col = (clue.pos - 1) % 5;
        int row = (clue.pos - 1) / 5;

        printGrid();
        for (int index = 0; index < 10; index++) {
            printGrid();
            if (clue.type == Clue.ACROSS) {
                for (int i = 0; i < clue.answerLength; i++) {
                    printGrid();
                    if (grid[row][col + i].equals("0")) {
                        grid[row][col + i] = "" + clue.candidates.get(index).word.charAt(i);

                        int y = row;
                        int x = col + 1;

                        int idx = (y * 5) + x + 1;
                        clue.editedAfter.add(idx);

                        if (i == clue.answerLength - 1) {
                            ArrayList<Clue> nexts = findClues(clue);
                            if (!nexts.isEmpty())
                                recursion(nexts);
                        }
                    }
                    else if (grid[row][col + i] == "" + clue.candidates.get(index).word.charAt(i)) {
                        grid[row][col + i] = "" + clue.candidates.get(index).word.charAt(i);

                        if (i == clue.answerLength - 1) {
                            ArrayList<Clue> nexts = findClues(clue);
                            if (!nexts.isEmpty())
                                recursion(nexts);
                        }
                    }
                    else {
                        for (int idx : clue.editedAfter) {
                            int drow = (idx - 1) / 5;
                            int dcol = (idx - 1) % 5;

                            grid[drow][dcol] = "0";
                        }
                        clue.editedAfter = new ArrayList<>();

                        if (i == clue.answerLength - 1) {
                            recursion(testCase);
                        }
                    }
                }
            }
            else if (clue.type == Clue.DOWN) {
                printGrid();
                for (int i = 0; i < clue.answerLength; i++) {
                    printGrid();
                    if (grid[row][col + i].equals("0")) {
                        grid[row + i][col] = "" + clue.candidates.get(index).word.charAt(i);

                        int y = row;
                        int x = col + 1;

                        int idx = (y * 5) + x + 1;
                        clue.editedAfter.add(idx);

                        if (i == clue.answerLength - 1) {
                            ArrayList<Clue> nexts = findClues(clue);
                            if (!nexts.isEmpty())
                                recursion(nexts);
                        }
                    }
                    else if (grid[row + i][col]  == "" + clue.candidates.get(index).word.charAt(i)) {
                        grid[row + i][col] = "" + clue.candidates.get(index).word.charAt(i);

                        if (i == clue.answerLength - 1) {
                            ArrayList<Clue> nexts = findClues(clue);
                            if (!nexts.isEmpty())
                                recursion(nexts);
                        }
                    }
                    else {
                        for (int idx : clue.editedAfter) {
                            int drow = (idx - 1) / 5;
                            int dcol = (idx - 1) % 5;

                            grid[drow][dcol] = "0";
                        }
                        clue.editedAfter = new ArrayList<>();

                        if (i == clue.answerLength - 1) {
                            recursion(testCase);
                        }
                    }
                }
            }

           /* ArrayList<Clue> nexts = findClues(clue);
            if (!nexts.isEmpty())
                recursion(nexts);
            else {
                clue

            }
*/
        }
    }

    private boolean isGridFull() {
        boolean check = true;
        for (int  i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (grid[i][j].equals("0"))
                    check = false;
            }
        }
        return check;
    }

    private ArrayList<Clue> findClues(Clue clue) {
        ArrayList<Clue> result = new ArrayList<>();

        int col = (clue.pos - 1) % 5;
        int row = (clue.pos - 1) / 5;

        if (clue.type == Clue.ACROSS) {
            int start1 = col;
            int end1 = start1 + clue.answerLength - 1;

            for (int i = 0; i < clues.size(); i++) {
                int colp = (clues.get(i).pos - 1) % 5;
                int rowp = (clues.get(i).pos - 1) / 5;

                int start2 = rowp;
                int end2 = start2 + clues.get(i).answerLength - 1;

                if ((start1 <= colp && colp <= end1) && (start2 <= row && row <= end2))
                    result.add(clues.get(i));
            }
        }
        else if (clue.type == Clue.DOWN) {
            int start1 = row;
            int end1 = start1 + clue.answerLength - 1;

            for (int i = 0; i < clues.size(); i++) {
                int colp = (clues.get(i).pos - 1) % 5;
                int rowp = (clues.get(i).pos - 1) / 5;

                int start2 = colp;
                int end2 = start2 + clues.get(i).answerLength - 1;

                if ((start1 <= rowp && rowp <= end1) && (start2 <= col && col <= end2))
                    result.add(clues.get(i));
            }
        }

        result.remove(clue);
        return result;
    }
}

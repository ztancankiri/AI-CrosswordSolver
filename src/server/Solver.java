package server;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

public class Solver {

    private ArrayList<Clue> clues;

    private String[][] grid;

    private PyExecutor antonymEx;
    private PyExecutor pluralizeEx;
    private PyExecutor singularizeEx;
    private PyExecutor synonymEx;

    private HashSet<Clue> isVisited;

    public String[][] finishedGrid;

    public Solver() {
        this.clues = new ArrayList<>();
        this.grid = new String[5][5];
        this.finishedGrid = new String[5][5];
        this.isVisited = new HashSet<>();

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                grid[i][j] = "0";

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                finishedGrid[i][j] = "0";

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
        }

        System.out.println();
        System.out.println("Candidate Lists are populated.");

        System.out.println();
        System.out.println("Puzzle solving...");

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

    public void calculateAnswers() {
        for (Clue clue : clues) {
            int col = (clue.pos - 1) % 5;
            int row = (clue.pos - 1) / 5;

            if (clue.type == Clue.ACROSS) {
                int start = col;
                int end = start + clue.answerLength - 1;

                for (int i = start; i <= end; i++)
                    clue.answer += finishedGrid[row][i];
            }
            else if (clue.type == Clue.DOWN) {
                int start = row;
                int end = start + clue.answerLength - 1;

                for (int i = start; i <= end; i++)
                    clue.answer += finishedGrid[i][col];
            }

            System.out.println(clue.answer);
        }
    }

    public void reset() {
        this.clues = new ArrayList<>();
        this.grid = new String[5][5];

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                grid[i][j] = "0";
    }

    public void solvePuzzle() {
        calculateAnswers();

        for (Clue clue : clues) {
            ArrayList<Word> temp = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                clue.candidates.set(0, new Word(clue.answer, "SRC"));
                temp.add(clue.candidates.get(i));
            }

            clue.candidates = temp;
        }

        System.out.println();
        System.out.println("Started to solving...");
        putToGrid(clues.get(0));

        System.out.println();
        System.out.println("Puzzle is solved...");
    }

    private void putToGrid(Clue clue) {
        int col = (clue.pos - 1) % 5;
        int row = (clue.pos - 1) / 5;

        while (!clue.candidates.isEmpty()) {
            Word candidate = clue.candidates.get(0);

            if (clue.type == Clue.ACROSS) {
                boolean isWritten = writeToGridAcross(candidate, clue);

                if (isWritten) {
                    isVisited.add(clue);
                    for (Clue next : findNextClues(clue)) {
                        if (!isGridFull())
                            putToGrid(next);
                        else
                            return;
                    }
                }
            }
            else if (clue.type == Clue.DOWN) {
                boolean isWritten = writeToGridDown(candidate, clue);

                if (isWritten) {
                    isVisited.add(clue);
                    for (Clue next : findNextClues(clue))
                        if (!isGridFull())
                            putToGrid(next);
                        else
                            return;
                }
            }
        }
    }

    private boolean writeToGridAcross(Word candidate, Clue clue) {
        int col = (clue.pos - 1) % 5;
        int row = (clue.pos - 1) / 5;

        for (int i = 0; i < clue.answerLength; i++) {
            if (grid[row][col + i].equals("0")) {
                grid[row][col + i] = candidate.word.charAt(i) + "";
                addToBeRemoveds(clue, row, col + i);
                printGrid();
            }
            else if (grid[row][col + i].equals(candidate.word.charAt(i) + "")) {
                printGrid();
            }
            else {
                removeFromGrid(clue);
                removeCandidate(candidate, clue);
                isVisited.remove(clue);
                printGrid();
                return false;
            }
        }
        return true;
    }

    private boolean writeToGridDown(Word candidate, Clue clue) {
        int col = (clue.pos - 1) % 5;
        int row = (clue.pos - 1) / 5;

        for (int i = 0; i < clue.answerLength; i++) {
            if (grid[row + i][col].equals("0")) {
                grid[row + i][col] = candidate.word.charAt(i) + "";
                addToBeRemoveds(clue, row + i, col);
                printGrid();
            }
            else if (grid[row + i][col].equals(candidate.word.charAt(i) + "")) {
                printGrid();
            }
            else {
                removeFromGrid(clue);
                removeCandidate(candidate, clue);
                isVisited.remove(clue);
                printGrid();
                return false;
            }
        }
        return true;
    }

    private void removeFromGrid(Clue clue) {
        for (int toBeRemoved : clue.editedAfter) {
            int col = (toBeRemoved - 1) % 5;
            int row = (toBeRemoved - 1) / 5;

            grid[row][col] = "0";
        }
        clue.editedAfter.clear();
    }

    private ArrayList<Clue> findNextClues(Clue clue) {
        ArrayList<Clue> result = new ArrayList<>();

        int col = (clue.pos - 1) % 5;
        int row = (clue.pos - 1) / 5;

        if (clue.type == Clue.ACROSS) {
            int start1 = col;
            int end1 = start1 + clue.answerLength - 1;

            for (int i = 0; i < clues.size(); i++) {
                if (clues.get(i) != clue && !isVisited.contains(clues.get(i))) {
                    int colp = (clues.get(i).pos - 1) % 5;
                    int rowp = (clues.get(i).pos - 1) / 5;

                    int start2 = rowp;
                    int end2 = start2 + clues.get(i).answerLength - 1;

                    if ((start1 <= colp && colp <= end1) && (start2 <= row && row <= end2))
                        result.add(clues.get(i));
                }
            }
        }
        else if (clue.type == Clue.DOWN) {
            int start1 = row;
            int end1 = start1 + clue.answerLength - 1;

            for (int i = 0; i < clues.size(); i++) {
                if (clues.get(i) != clue && !isVisited.contains(clues.get(i))) {
                    int colp = (clues.get(i).pos - 1) % 5;
                    int rowp = (clues.get(i).pos - 1) / 5;

                    int start2 = colp;
                    int end2 = start2 + clues.get(i).answerLength - 1;

                    if ((start1 <= rowp && rowp <= end1) && (start2 <= col && col <= end2))
                        result.add(clues.get(i));
                }
            }
        }

        return result;
    }

    private void addToBeRemoveds(Clue clue, int row, int col) {
        int idx = (row * 5) + col + 1;
        clue.editedAfter.add(idx);
    }

    private void removeCandidate(Word candidate, Clue clue) {
        clue.candidates.remove(candidate);
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

    private void printGrid() {
        System.out.println();
        System.out.println("---GRID---");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                System.out.print(grid[i][j] + "\t");

            System.out.println();
        }
    }
}

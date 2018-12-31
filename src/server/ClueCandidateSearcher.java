package server;

import java.util.ArrayList;

public class ClueCandidateSearcher extends Thread {

    private Clue clue;
    private ArrayList<Word> collection;

    public ClueCandidateSearcher(Clue clue) {
        this.clue = clue;
        this.collection = new ArrayList<>();
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        System.out.println("Google Search");
        GoogleSearcher searcher = new GoogleSearcher();
        searcher.search(clue.question);

        ArrayList<Word> temp = new ArrayList<>();

        System.out.println("Google Links got.");
        for (String url : searcher.getSearchResults()) {
            if (!url.contains("wikipedia")) {
                System.out.println("Website Parsing. -> " + url);
                WebsiteWordLister wordLister = new WebsiteWordLister(url);
                wordLister.getWords();
                temp.addAll(wordLister.getWordList());
                System.out.println("Website parsed.");
            }
        }

        collection.addAll(temp);
        interrupt();
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    public ArrayList<Word> getCollection() {
        return this.collection;
    }

    public Clue getClue() {
        return this.clue;
    }
}

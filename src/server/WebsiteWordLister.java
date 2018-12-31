package server;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class WebsiteWordLister {

    private String searchURL;
    private ArrayList<Word> wordList;

    public WebsiteWordLister(String searchURL) {
        this.searchURL = searchURL;
        this.wordList = new ArrayList<>();
    }

    public void getWords() {
        try {
            Document document = Jsoup.connect(searchURL).get();
            String text = document.text().replaceAll("[^a-zA-Z\\s]", "");
            for (int i = 0; i < 5; i++)
                text = text.replace("  ", " ");

            String[] parsed = text.split(" ");

            for (String element : parsed)
                wordList.add(new Word(element.toUpperCase(), searchURL));

        } catch (IOException e) {
            System.out.println("» Exception: " + e.getMessage());
        }
    }

    public void printWordList() {
        for (Word element : wordList)
            System.out.println(element.word + " - " + element.source);

        System.out.println();
    }

    public ArrayList<Word> getWordList() {
        return wordList;
    }

}

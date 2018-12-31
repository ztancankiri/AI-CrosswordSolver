package server;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class Test  {

    public static void main(String[] args) throws IOException, InterruptedException {
/*        GoogleSearcher searcher = new GoogleSearcher();
        searcher.search("Hard-to-find guy in a crowd");

        ArrayList<Word> candidateList = new ArrayList<>();

        for (String url : searcher.getSearchResults()) {
            if (!url.contains("wikipedia")) {
                WebsiteWordLister wordLister = new WebsiteWordLister(url);
                wordLister.getWords();
                candidateList.addAll(wordLister.getWordList());
            }
        }

        System.out.println();
        System.out.println("Total Word Count: " + candidateList.size());

        CandidateFilter filter = new CandidateFilter(candidateList);
        ArrayList<Word> words_2 = filter.filterByLength(2);
        ArrayList<Word> words_3 = filter.filterByLength(3);
        ArrayList<Word> words_4 = filter.filterByLength(4);
        ArrayList<Word> words_5 = filter.filterByLength(5);

 *//*       CandidateFilter filter2 = new CandidateFilter(candidateList);
        ArrayList<Word> charFiltered = filter2.filterByChar(new CF('C',0), new CF('E',2));*//*

        ArrayList<Word> merged = new ArrayList<>();
        merged.addAll(words_2);
        merged.addAll(words_3);
        merged.addAll(words_4);
        merged.addAll(words_5);

        CandidateFilter stowordRemover = new CandidateFilter(words_5);

        ArrayList<Word> noStopword = stowordRemover.removeStopwords();

        CandidateFilter cleaner = new CandidateFilter(noStopword);
        ArrayList<Word> cleaned = cleaner.cleanReplicates();

        for (int i = 0; i < cleaned.size(); i++) {
            Word w = cleaned.get(i);
            System.out.println(i + " - " + w.word + " (" + w.freq + ") -> " + w.source);
        }*/
/*
        PyExecutor executor = new PyExecutor("python3", "antonyms.py");
        String result = executor.exec("energy");
        System.out.println(result);*/

     /*   ThesaurusScraper thesaurusScraper = new ThesaurusScraper("bad");
        thesaurusScraper.get();
        ArrayList<String> list_s = thesaurusScraper.getSynonyms();
        ArrayList<String> list_a = thesaurusScraper.getAntonyms();

        System.out.println("Synonyms:");

        for (String syn : list_s) {
            System.out.println(syn);
        }

        System.out.println();
        System.out.println("Antonyms:");

        for (String ant : list_a) {
            System.out.println(ant);
        }*/


        /*PyExecutor executor = new PyExecutor("python3", "pluralize.py");

        ArrayList<Word> words = new ArrayList<>();
        words.add(new Word("man", ""));
        words.add(new Word("dog", ""));
        words.add(new Word("woman", ""));
        words.add(new Word("book", ""));
        words.add(new Word("girls", ""));
        words.add(new Word("fantasy", ""));
        words.add(new Word("fish", ""));

        JSONArray result = executor.exec(words);

        for (Object obj : result) {
            JSONObject object = (JSONObject) obj;
            String word = object.getString("word");
            String plural = object.getString("plural");

            System.out.println(word + " -> " + plural);
        }*/

        /*PyExecutor e = new PyExecutor("python3", "test.py");
        System.out.println(e.exec(""));*/

        /*String clueText = "San Diego ballplayer";

        String url = clueText.replace(" ", "-").toLowerCase();
        url = "http://crosswordtracker.com/clue/" + url;
        Document document = Jsoup.connect(url).get();
        Element node = document.getElementsByClass("answer highlighted").first();
        String text = node.text();
        System.out.println(text);*/


        long counter = 0;
        for (int i = 0; i < 10000; i++) {
            for (int j = 0; j < 10000; j++) {
                for (int k = 0; k < 100; k++) {
                    counter++;
                    System.out.println(counter);
                }
            }
        }
    }
}

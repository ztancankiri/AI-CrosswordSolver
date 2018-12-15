package server;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class ThesaurusScraper {

    private static final String THESAURUS_URL = "https://www.thesaurus.com/browse/";

    private String word;
    private ArrayList<String> synonyms;
    private ArrayList<String> antonyms;

    // //*[@id="initial-load-content"]/main/section/section/section[3]/ul
    // //*[@id="initial-load-content"]/main/section/section/section[1]/ul
    public ThesaurusScraper(String word) {
        this.synonyms = new ArrayList<>();
        this.antonyms = new ArrayList<>();
        this.word = word;
    }

    public void get() {

        String url = THESAURUS_URL + word;

        try {
            Document document = Jsoup.connect(url).get();
            Element initial = document.getElementById("initial-load-content");
            Element main = initial.getElementsByTag("main").first();
            Element section = main.getElementsByTag("section").first();
            Element section2 = section.getElementsByTag("section").get(2);
            Element ul = section2.getElementsByTag("ul").first();

            Elements lis = ul.getElementsByTag("li");

            for (Element li : lis) {
                String synonym = li.text();
                synonyms.add(synonym);
            }

            section2 = section.getElementsByTag("section").get(4);
            ul = section2.getElementsByTag("ul").first();

            lis = ul.getElementsByTag("li");

            for (Element li : lis) {
                String antonym = li.text();
                antonyms.add(antonym);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getSynonyms() {
        return this.synonyms;
    }

    public ArrayList<String> getAntonyms() {
        return this.antonyms;
    }
}

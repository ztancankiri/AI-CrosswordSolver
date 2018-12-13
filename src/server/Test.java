package server;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class Test implements MessageListener {

    public static void main(String[] args) throws IOException, InterruptedException {
        /*        GoogleSearcher searcher = new GoogleSearcher();
        searcher.search("master of puppets");

        for (String url : searcher.getSearchResults()) {
            if (!url.contains("wikipedia")) {
                WebsiteWordLister wordLister = new WebsiteWordLister(url);
                wordLister.getWords();
                wordLister.printWordList();
            }
        }*/

        PyConnector connector = new PyConnector("localhost", 4444, new Test());
        connector.start();

        JSONObject json = new JSONObject();
        json.put("cmd", "antonyms");
        json.put("word", "bad");
        connector.sendMessage(json.toString());

        json.put("cmd", "synonyms");
        json.put("word", "man");
        connector.sendMessage(json.toString());


        Thread.sleep(5000);
        json.put("cmd", "closeModule");
        connector.sendMessage(json.toString());
    }

    @Override
    public void onMessageReceived(String message) {
        JSONObject json = new JSONObject(message);

        if (json.getString("cmd").equals("synonymsResponse")) {
            System.out.println("Word: " + json.getString("word"));
            JSONArray results = json.getJSONArray("results");

            for (Object result : results) {
                System.out.println((String) result);
            }

            System.out.println();
        }
        else if (json.getString("cmd").equals("antonymsResponse")) {
            System.out.println("Word: " + json.getString("word"));
            JSONArray results = json.getJSONArray("results");

            for (Object result : results) {
                System.out.println((String) result);
            }

            System.out.println();
        }
    }
}

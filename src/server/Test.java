package server;

import java.io.IOException;

public class Test  {

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

        /*PyConnector connector = new PyConnector("localhost", 4444, new Test());
        connector.start();

        JSONObject json = new JSONObject();
        json.put("cmd", "antonyms");
        json.put("word", "bad");
        connector.sendMessage(json.toString());

        Thread.sleep(1000);

        json.put("cmd", "synonyms");
        json.put("word", "man");
        connector.sendMessage(json.toString());

        Thread.sleep(1000);

        json.put("cmd", "pluralize");
        json.put("word", "man");
        connector.sendMessage(json.toString());

        Thread.sleep(400);

        Thread.sleep(5000);
        json.put("cmd", "closeModule");
        connector.sendMessage(json.toString());*/


        PyExecutor executor = new PyExecutor("python3", "synonyms.py");
        String result = executor.exec("man");
        System.out.println(result);

    }

   /* @Override
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
        else if (json.getString("cmd").equals("pluralizeResponse")) {
            System.out.println("Word: " + json.getString("word"));
            System.out.println("Plural: " + json.getString("result"));

            System.out.println();
        }
    }*/
}

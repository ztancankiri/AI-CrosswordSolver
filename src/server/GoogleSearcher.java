package server;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class GoogleSearcher {

    private static final String SEARCH_URL = "https://www.google.com/search?hl=en&num=50&q=";

    private static final String[] FORBIDDEN_URL_PARTS = {
            "webcache.googleusercontent",
            "translate.google.com",
            "youtube.com",
            "/preferences",
            "/setprefs?safeui",
            "/advanced_search?q=",
            "/history/privacyadvisor/search?",
            "/history/optout?hl=",
            "//support.google.com/websearch/",
            "/imgres?imgurl=",
            "https://accounts.google.com/ServiceLogin?hl=",
            "/about/products?tab=wh",
            "https://www.google.com/flights?q=",
            "https://maps.google.com/maps?q=",
            "https://www.google.com/webhp?",
            "https://maps.google.com/maps?",
            "https://support.google.com/websearch?p=ws_settings_location",
            "https://www.google.com/intl",
            "google.com.tr/shopping?",
            "jamboard.google.com",
            "wordplays.com"
    };

    private static final String[] FORBIDDEN_URLS = {
            "",
            "#",
            "https://www.blogger.com/?tab=wj",
            "https://hangouts.google.com/",
            "https://keep.google.com/",
            "https://earth.google.com/web/",
            "https://www.google.com.tr/save",
            "https://docs.google.com/document/?usp=docs_alc",
            "https://photos.google.com/?tab=wq&pageId=none",
            "https://plus.google.com/?gpsrc=ogpy0&tab=wX",
            "https://www.google.com/calendar?tab=wc",
            "https://drive.google.com/?tab=wo",
            "https://contacts.google.com/?hl=tr&tab=wC",
            "https://mail.google.com/mail/?tab=wm",
            "https://news.google.com/nwshp?hl=en&tab=wn",
            "https://play.google.com/?hl=en&tab=w8",
            "https://maps.google.com/maps?hl=en&tab=wl",
            "https://www.google.com/webhp?tab=ww",
            "https://myaccount.google.com/?utm_source=OGB&utm_medium=app",
            "https://posts.google.com/claim/?mid=/m/05qbbfb&refui=b",
            "https://contacts.google.com/?hl=en&tab=wC",
            "https://maps.google.com.tr/maps?hl=en&tab=wl",
            "https://www.google.com.tr/webhp?tab=ww"
    };

    private static final String[] FORBIDDEN_PREFIXES = {
            "/search?hl=en&",
            "/url?url=",
            "javascript:",
            "/search?"
    };

    private static final String[] FORBIDDEN_POSTFIXES = {
            ".pdf",
            ".doc",
            ".docx",
            ".ppt",
            ".xls",
            ".pptx",
            ".xlsx"
    };

    private String searchText;
    private String searchURL;

    private ArrayList<String> searchResults;

    public GoogleSearcher() {
        this.searchText = "";
        this.searchResults = new ArrayList<>();
    }

    public void search(String searchText) {
        this.searchText = searchText;
        createSearchURL();

        try {
            Document document = Jsoup.connect(searchURL).get();
            Elements aTags = document.getElementsByTag("a");

            for (Element a : aTags) {
                String aHref = a.attr("href");
                if (!isForbidden(aHref))
                    searchResults.add(aHref);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fixSearchText() {
        searchText = searchText.replace(" ", "+");
    }

    private void createSearchURL() {
        fixSearchText();
        searchURL = SEARCH_URL + searchText;
    }

    public void printSearchResults() {
        System.out.println("---Search Results---");

        for (String element : searchResults)
            System.out.println(element);

        System.out.println();
    }

    private boolean isForbidden(String url) {
        for (int i = 0; i < FORBIDDEN_URL_PARTS.length; i++) {
            if (url.contains(FORBIDDEN_URL_PARTS[i]))
                return true;
        }

        for (int i = 0; i < FORBIDDEN_URLS.length; i++) {
            if (url.equals(FORBIDDEN_URLS[i]))
                return true;
        }

        for (int i = 0; i < FORBIDDEN_PREFIXES.length; i++) {
            if (url.startsWith(FORBIDDEN_PREFIXES[i]))
                return true;
        }

        for (int i = 0; i < FORBIDDEN_POSTFIXES.length; i++) {
            if (url.endsWith(FORBIDDEN_POSTFIXES[i]))
                return true;
        }

        if (!url.startsWith("http"))
            return true;

        return false;
    }

    public ArrayList<String> getSearchResults() {
        return searchResults;
    }

}

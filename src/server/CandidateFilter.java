package server;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class CandidateFilter {

    public static final String[] ENGLISH_STOPWORDS = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "you're", "you've", "you'll", "you'd", "your",
            "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "she's", "her", "hers", "herself", "it", "it's", "its", "itself", "they", "them", "their",
            "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "that'll", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have",
            "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with",
            "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over",
            "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some",
            "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "don't", "should", "should've", "now", "d",
            "ll", "m", "o", "re", "ve", "y", "ain", "aren", "aren't", "couldn", "couldn't", "didn", "didn't", "doesn", "doesn't", "hadn", "hadn't", "hasn", "hasn't",
            "haven", "haven't", "isn", "isn't", "ma", "mightn", "mightn't", "mustn", "mustn't", "needn", "needn't", "shan", "shan't", "shouldn", "shouldn't",
            "wasn", "wasn't", "weren", "weren't", "won", "won't", "wouldn", "wouldn't"};

    private ArrayList<Word> list;

    public CandidateFilter(ArrayList<Word> list) {
        this.list = list;
    }

    public ArrayList<Word> filterByLength(int n) {
        ArrayList<Word> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Word word = list.get(i);

            if (word.word.length() == n) {
                result.add(word);
            }
        }

        return result;
    }

    public ArrayList<Word> filterByChar(CF... cfs) {
        ArrayList<Word> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Word word = list.get(i);

            boolean check = true;
            for (int j = 0; j < cfs.length; j++) {
                if (cfs[j].pos < word.word.length()) {
                    if (word.word.charAt(cfs[j].pos) != cfs[j].character)
                        check = false;
                }
                else
                    check = false;
            }

            if (check)
                result.add(word);
        }

        return result;
    }

    public ArrayList<Word> cleanReplicates() {
        ArrayList<Word> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Word word = list.get(i);
            int index = listContains(result, word.word);
            if (index != -1) {
                result.get(index).incFreq();
            }
            else {
                result.add(word);
            }
        }

        Comparator<Word> comparator = new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                return Integer.compare(o2.freq, o1.freq);
            }
        };

        result.sort(comparator);

        return result;
    }

    public ArrayList<Word> removeStopwords() {
        ArrayList<Word> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Word word = list.get(i);

            boolean check = true;
            for (int j = 0; j < ENGLISH_STOPWORDS.length; j++) {
                if (word.word.equals(ENGLISH_STOPWORDS[j].toUpperCase()) || word.word.equals(ENGLISH_STOPWORDS[j].toUpperCase().replace("'", "")) || word.word.equals(ENGLISH_STOPWORDS[j].toUpperCase().replace("-", ""))) {
                    check = false;
                }
            }

            if (check)
                result.add(word);
        }

        return result;
    }

    public ArrayList<Word> removeClueWords(String clueText) {
        ArrayList<Word> result = new ArrayList<>();

        String text = clueText.toUpperCase().replaceAll("[^a-zA-Z\\s]", "");

        for (int i = 0; i < 5; i++)
            text = text.replace("  ", " ");

        String[] splitted = text.split(" ");

        for (int i = 0; i < list.size(); i++) {
            Word w = list.get(i);

            boolean check = true;
            for (int j = 0; j < splitted.length; j++) {
                if (w.word.equals(splitted[j]))
                    check = false;
            }

            if (check)
                result.add(w);
        }

        return result;
    }

    public ArrayList<Word> filter(String clueText) {
        ArrayList<Word> result = new ArrayList<>();

        try {
            String url = clueText.replace(" ", "-").toLowerCase();
            url = "http://crosswordtracker.com/clue/" + url;
            Document document = Jsoup.connect(url).get();
            Element node = document.getElementsByClass("answer highlighted").first();
            String text = node.text();

            for (Word w : list)
                result.add(w);

            Random rand = new Random();
            int n = rand.nextInt(9);
            int prevFreq = result.get(n).freq;

            int index = listContains(result, text);
            if (index != -1) {
                if (index > n) {
                    result.get(index).freq = prevFreq + 54;
                    result.get(index).source = "CT";
                }
            }
            else {
                Word word = new Word(text, "CT");
                word.freq = prevFreq + 54;
                result.add(word);
            }

            Comparator<Word> comparator = new Comparator<Word>() {
                @Override
                public int compare(Word o1, Word o2) {
                    return Integer.compare(o2.freq, o1.freq);
                }
            };

            result.sort(comparator);
            return result;
        }
        catch (IOException e) {}

        for (Word w : list)
            result.add(w);

        return result;
    }

    private int listContains(ArrayList<Word> myList, String word) {
        for (int i = 0; i < myList.size(); i++) {
            if (myList.get(i).word.equals(word)) {
                return i;
            }
        }
        return -1;
    }

}

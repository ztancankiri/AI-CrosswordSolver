package server;

public class Word {

    public String word;
    public String source;
    public int freq;

    public Word(String word, String source) {
        this.word = word;
        this.source = source;
        this.freq = 1;
    }

    public void incFreq() {
        this.freq++;
    }
}

package utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordHelper {
    private static List<String> stopWords = null;


    public static List<String>getStopWords() {
        if(stopWords == null) {
            try {

                stopWords = Files.readAllLines(Paths.get("./assets/stopwords.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stopWords;
    }
    public static List<String> prepareString (String str) {
        str = str.replaceAll("[^a-zA-Z0-9]", " ");
        str = str.toLowerCase();
        String [] wordsTemp = str.split("\\s+");

        List<String> words = new ArrayList<String>();
        Collections.addAll(words, wordsTemp);

        return words;
    }

    public static List<String> processString (String str) {
        str = str.replaceAll("[^a-zA-Z0-9]", " ");
        str = str.toLowerCase();
        String [] wordsTemp = str.split("\\s+");

        getStopWords();
        PorterStemmer stemmer = new PorterStemmer();
        List<String> words = new ArrayList<String>();

        for (String s : wordsTemp) {
            if (!stopWords.contains(s) && !s.trim().isEmpty()) {
                words.add(stemmer.stem(s));
            }
        }
        return words;
    }
}

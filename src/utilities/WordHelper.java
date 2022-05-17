package utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    public static List<String> processString (String str) {
        str = str.replaceAll("[^a-zA-Z0-9]", " ");
        str = str.toLowerCase();
        String [] wordsTemp = str.split("\\s+");

        getStopWords();
        Stemmer stemmer = new Stemmer();
        List<String> words = new ArrayList<String>();

        for (String s : wordsTemp) {
            if (!stopWords.contains(s) && !s.trim().isEmpty()) {
                stemmer.add(stringToCharArray(s), s.length());
                stemmer.stem();
                words.add(stemmer.toString());
            }
        }

        return words;
    }

    private static char[] stringToCharArray(String str) {
        char[] ch = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            ch[i] = str.charAt(i);
        }
        return ch;
    }
}

package utilities;

import java.util.HashMap;

public class Constants {
    private static boolean is_initialized = false;

    public static final String DATABASE_NAME = "webCrawlerDB";
    public static HashMap<String, Integer> indexerWeight = new HashMap<String, Integer>();
    public static void initialize() {
        if (is_initialized) return;
        indexerWeight.put("title", 70);
        indexerWeight.put("h1", 50);
        indexerWeight.put("h2", 30);
        indexerWeight.put("h3", 20);
        indexerWeight.put("h4", 10);
        indexerWeight.put("h5", 8);
        indexerWeight.put("h6", 7);
        indexerWeight.put("strong", 6);
        indexerWeight.put("em", 6);
        indexerWeight.put("a", 5);
        indexerWeight.put("i", 4);
        indexerWeight.put("b", 4);
        indexerWeight.put("td", 4);
        indexerWeight.put("th", 4);
        indexerWeight.put("li", 4);
        indexerWeight.put("p", 2);
    }
}

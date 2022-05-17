package indexer;
import DB.DBMan;
import utilities.Constants;
import utilities.WordHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {
    public static void main(String [] args) throws IOException {

        //List <Integer> list = calculateLinkNumber(8, 1027);
        //System.out.println();
        /*
        long test = 10;
        int stat = (int) test;
        */


        DBMan.startConnection("test");
        //DBMan.test_crawler_insert(link, doc.html(), "true", "false");
        DBMan.test_indexer_insert();
        DBMan.endConnection();

        /*
        List<String> words = new ArrayList<>();
        words.add("mohamed");
        words.add("abdo");
        words.add("mohamed");
        words.add("ahmed");
        words.add("mohamed");
        HashMap<String, Pairing> hm = new HashMap<String, Pairing>();

        for (String word: words) {
            Pairing defaultPairing = new Pairing(0,0);
            Pairing pairingValue = hm.getOrDefault(word, defaultPairing);
            if (word.equals("mohamed"))
                pairingValue.value += 1000;
            pairingValue.wordCount += 1;
            hm.put(word, pairingValue);
        }
        System.out.println("");
         */


        /*
        List<String> stringList = new ArrayList<String>();
        stringList.add("https://towardsdatascience.com/quickly-extract-all-links-from-a-web-page-using-javascript-and-the-browser-console-49bb6f48127b");
        stringList.add("https://en.wikipedia.org/wiki/Main_Page");
        stringList.add("https://en.wikipedia.org/wiki/Colossal_Cave_Adventure");
        stringList.add("https://en.wikipedia.org/wiki/William_Crowther_(programmer)");
        stringList.add("https://en.wikipedia.org/wiki/Wikipedia:Contents");
        stringList.add("https://donate.wikimedia.org/w/index.php?title=Special:LandingPage&country=XX&uselang=en&utm_medium=sidebar&utm_source=donate&utm_campaign=C13_en.wikipedia.org");
        stringList.add("https://en.wikipedia.org/wiki/Wikipedia:Contents/Natural_and_physical_sciences");
        stringList.add("https://en.wikipedia.org/wiki/Portal:Science");
        stringList.add("https://en.wikipedia.org/wiki/Science");
        */
        /*
        for (String link: stringList) {
            org.jsoup.nodes.Document doc = Jsoup.connect(link).get();
            DBMan.test_crawler_insert(link, doc.html(), "true", "false");
        }
        */

        /*
        String link = "https://en.wikipedia.org/wiki/Science";
        org.jsoup.nodes.Document doc = Jsoup.connect(link).get();
        DBMan.startConnection("test");
        //DBMan.test_crawler_insert(link, doc.html(), "true", "false");
        DBMan.test_unknown();
        DBMan.endConnection();
        */

        /*
         */
         /*
        HashMap<String, Integer> wordCountMap = new HashMap<String, Integer>();
        String str1 = "ahmed is very well-driven to do his job and eat all monsters"
                + " and he is the ahmed of all ahmeds and he create them all";
        String str2 = "";
        List<String> words = WordHelper.processString(str1, str2);

        processWordCount(words,wordCountMap);

        System.out.println("");
         */

        //Integer intr = null;
        //intr = 0;
        //System.out.println(intr+1);
        //List<String> stringList = WordHelper.getStopWords();
        //System.out.println("hello");

        /*
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);

        File input = new File("./test_assets/test.html");
        org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8", "");

        String test_1 = doc.body().html();
        System.out.println(test_1);

        DBMan.startConnection("test");
        //DBMan.test_insert(test_1);
        DBMan.test_iterable();
        DBMan.endConnection();
        */

        /*
        int x = 9;
        int y = 2;
        int multiplier = Double.valueOf(
                Math.ceil((double) x /
                        (double) y)
        ).intValue();
        System.out.println(multiplier);
         */
    }

    private static void processWordCount (List<String> words,
                                          HashMap<String, Integer> wordMap) {
        for (String word : words) {
            Integer wordCount = wordMap.getOrDefault(word, null);
            if (wordCount == null) {
                wordCount = 1;
            } else {
                wordCount++;
            }
            wordMap.put(word, wordCount);
        }
    }

    private static List<Integer> calculateLinkNumber (int thread_number, int total_number) {
        int docs_per_thread = Double.valueOf(Math.ceil((double) total_number
                / (double) thread_number)).intValue();

        List<Integer> list = new ArrayList<Integer>();
        int current_number = total_number;

        while(current_number > 0) {
            if (current_number > docs_per_thread) {
                list.add(docs_per_thread);
                current_number -= docs_per_thread;
            }
            else {
                list.add(current_number);
                current_number = 0;
            }
        }

        while (thread_number > list.size()) {
            list.add(-1);
        }
        return list;
    }
}

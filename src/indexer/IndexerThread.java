package indexer;

import DB.MongoDB;
import com.mongodb.client.FindIterable;
import org.jsoup.Jsoup;
import utilities.Constants;
import utilities.WordHelper;

import java.util.HashMap;
import java.util.List;

public class IndexerThread implements Runnable{
    MongoDB dbManager;
    private int start_index;
    private int end_index;
    private List<String> urls;

    public IndexerThread(MongoDB dbman, int s, int e, List<String> u) {
        dbManager = dbman;
        start_index = s;
        end_index = e;
        urls = u;
    }
    public void indexerRun(String url) {
        try {
            // inialize score and word size hash-map for the HTML Document
            HashMap<String, Pairing> documentMap = new HashMap<String, Pairing>();

            /* get HTML Document */
            FindIterable<org.bson.Document> iterable = dbManager.getOneNotIndexedLink(url);
            org.jsoup.nodes.Document doc = Jsoup.parse(
                    iterable.first().getOrDefault("html", "").toString());

            /* initialize initial score and word count for the HTML Document */
            Constants.initialize();
            for(String tag : Constants.indexerWeight.keySet()) {
                org.jsoup.select.Elements elements = doc.getElementsByTag(tag);

                for (org.jsoup.nodes.Element element : elements) {
                    List<String> words = WordHelper.processString(element.text());
                    for(String word : words) {
                        Pairing defaultPairing = new Pairing(0,0);
                        Pairing pairingValue = documentMap.getOrDefault(word, defaultPairing);

                        pairingValue.value += Constants.indexerWeight.get(tag);
                        documentMap.put(word, pairingValue);
                    }
                    words.clear();
                }
            }

            /* get HTML document title */
            String title = doc.title();
            if (title.isEmpty()) title = url;

            /* calculate word count and word value for the HTML Document */
            String text = doc.title() + " " + doc.body().text();
            List<String> words = WordHelper.processString(text);
            int totalWordCount = words.size();

            for (String word : words) {
                Pairing pairingValue = documentMap.getOrDefault(word, null);
                if (pairingValue == null) {
                    pairingValue = new Pairing(1, 1);
                    pairingValue.isAnotherTag = true;
                } else {
                    pairingValue.wordCount += 1;

                    if (pairingValue.isAnotherTag) {
                        pairingValue.value += 1;
                    }
                }
                documentMap.put(word, pairingValue);
            }

            /* send to database */
            dbManager.updateIndexerCollection(url, title, doc.body().text(),
                    documentMap, totalWordCount);

            //words.clear();
            //documentMap.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        if (end_index != -1 && urls.size() > 0) {
            for (int i = start_index; i <= end_index; i++) {
                indexerRun(urls.get(i));
            }
        }
    }
}

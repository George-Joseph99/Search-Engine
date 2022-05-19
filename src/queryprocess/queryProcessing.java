package queryprocess;

import DB.MongoDB;
import com.mongodb.client.AggregateIterable;
import utilities.WordHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class queryProcessing {
    MongoDB dbManager;
    public queryProcessing(MongoDB dbman) {
        dbManager = dbman;
    }

    public  List<RetrievedDocument> processTextQuery(String query) {
        List<String> processedQuery = WordHelper.processString(query);
        HashMap<String, org.bson.Document> tempHash = new HashMap<String, org.bson.Document>();

        for (String word : processedQuery)
        {
            AggregateIterable<org.bson.Document> documents = dbManager.getMatchedDocuments(word);

            for (org.bson.Document document : documents) {
                String url = document.get("url").toString();
                tempHash.put(url, document);
            }
        }

        List<RetrievedDocument> results = new ArrayList<>();
        for (org.bson.Document document : tempHash.values()) {
            String url = document.get("url").toString();
            String title = document.get("title").toString();
            String body = document.get("body").toString();
            int tf = Integer.parseInt(document.get("tf").toString());
            double score = Double.parseDouble(document.get("score").toString());
            int word_count = Integer.parseInt(document.get("word_count").toString());

            RetrievedDocument retrievedDocument = new RetrievedDocument(url, title,
                    body, tf, score, word_count);
            results.add(retrievedDocument);
        }

        return results;
    }
}

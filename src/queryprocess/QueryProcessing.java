package queryprocess;

import DB.MongoDB;
import com.mongodb.client.AggregateIterable;
import utilities.WordHelper;

import javax.swing.text.Document;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryProcessing {
    MongoDB dbManager;
    public QueryProcessing(MongoDB dbman) {
        dbManager = dbman;
    }

    public  List<RetrievedDocument> processTextQuery(String query) {
        List<String> processedQuery = WordHelper.processString(query);
        HashMap<String, org.bson.Document> tempHash = new HashMap<String, org.bson.Document>();
        HashMap<String, Integer> docFreqHash = new HashMap<String, Integer>();
        for (String word : processedQuery)
        {
            AggregateIterable<org.bson.Document> documents = dbManager.getMatchedDocuments(word);

            for (org.bson.Document document : documents) {
                String url = document.get("url").toString();
                tempHash.put(url, document);
                docFreqHash.put(url, dbManager.getDocumentFrequency(word));
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
            double pageRank = (double) document.get("pageRank");
            int numofDocs = dbManager.countCrawledDocuments();
            int df = docFreqHash.get(url);
            double idf = Math.log((double) numofDocs / df);

            RetrievedDocument retrievedDocument = new RetrievedDocument(url, title,
                    body, tf, idf, score, word_count, pageRank);
            results.add(retrievedDocument);
        }

        return results;
    }
}

package DB;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import indexer.Pairing;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBMan {
    private static final String CONNECTION_URI = "mongodb://localhost:27017";

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    private static MongoCollection<Document> CrawlerCollection;
    private static MongoCollection<Document> indexerCollection;
    private static MongoCollection<Document> productsCollection;

    public static void startConnection(String DBname) {
        if(mongoClient != null) {
            System.out.println("Already connected!");
            return;
        }

        try {
            ConnectionString connectionString = new ConnectionString(CONNECTION_URI);


            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .retryWrites(true)
                    .build();

            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(DBname);

            CrawlerCollection = database.getCollection("CrawlerCollection");
            productsCollection = database.getCollection("products");
            indexerCollection = database.getCollection("IndexerCollection");
            System.out.println("Connected to DB");


        }catch(Exception e) {
            System.out.println("Error connecting to DB");
            e.printStackTrace();
        }
    }

    public static void InsertUrl(String url ,String html) {
        try {
            CrawlerCollection.insertOne(new Document()
                    .append("_id", new ObjectId())
                    .append("url", url)
                    .append("crawled", "false")
                    .append("html", html)
            );
            //System.out.println("Success! Inserted document" );
        } catch (MongoException me) {
            System.err.println("Unable to insert due to an error: " +me);
        }

    }
    public static void InsertUrl(String url) {
        try {
            CrawlerCollection.insertOne(new Document()
                    .append("_id", new ObjectId())
                    .append("url", url)
                    .append("crawled", "false")
            );
            //System.out.println("Success! Inserted document" );
        } catch (MongoException me) {
            System.err.println("Unable to insert due to an error: " +me);
        }

    }
    public static void setCrawled(String url) {

        Bson updates = Updates.combine(Updates.set("crawled", "true"));
        Document query = new Document().append("url",  url);
        try {

            CrawlerCollection.updateOne(query, updates);
            //System.out.println("Success! updated document" );
        } catch (MongoException me) {
            System.err.println("Unable to update due to an error: " +me);
        }

    }
    public static void setContent(String url ,String content) {

        Bson updates = Updates.combine(Updates.set("html", content));
        Document query = new Document().append("url",  url);
        try {

            CrawlerCollection.updateOne(query, updates);
            //System.out.println("Success! updated document" );
        } catch (MongoException me) {
            System.err.println("Unable to update due to an error: " +me);
        }

    }
    public ArrayList<String> getURL(String url) {
        FindIterable<Document> iterable = CrawlerCollection.find(new org.bson.Document("url", url))
                .projection(Projections.include("url"));
        List<Document> results = new ArrayList<>();
        iterable.into(results);
        ArrayList<String> matchedUrls = new ArrayList<>();
        for (int i=0 ; i<results.size(); i++) {
            matchedUrls.add(results.get(i).get("url").toString());
        }
        return matchedUrls;
    }
    public  ArrayList<String> getCrawled() {
        FindIterable <Document> iterable = CrawlerCollection.find(new org.bson.Document("crawled", "true"))
                .projection(Projections.include("url"));
        List <Document> results = new ArrayList<>();
        iterable.into(results);
        ArrayList<String> crawledUrls = new ArrayList<>();
        for (int i=0 ; i<results.size(); i++) {
            crawledUrls.add(results.get(i).get("url").toString());
        }
        return crawledUrls;
    }
    public  ArrayList<String> getNotCrawled() {
        FindIterable <Document> iterable = CrawlerCollection.find(new org.bson.Document("crawled", "false"))
                .projection(Projections.include("url"));
        List <Document> results = new ArrayList<>();
        iterable.into(results);
        ArrayList<String> crawledUrls = new ArrayList<>();
        for (int i=0 ; i<results.size(); i++) {
            crawledUrls.add(results.get(i).get("url").toString());
        }
        return crawledUrls;
    }

    /////////////////////////////////////////////////////////////////
    ////////////////////////     INDEXER     ////////////////////////
    /////////////////////////////////////////////////////////////////
    public static FindIterable<org.bson.Document> getNotIndexedLinks() {
        return productsCollection.find(new org.bson.Document("crawled", "true")
                .append("indexed", "false"));
    }

    public static List<String> getNotIndexedURLS() {
        FindIterable<org.bson.Document> iterable = DBMan.getNotIndexedLinks();
        List<String> urls = new ArrayList<String>();
        for (org.bson.Document doc : iterable) {
            urls.add(doc.getOrDefault("url",null).toString());
        }
        return urls;
    }

    public static FindIterable<org.bson.Document> getOneNotIndexedLink(String link) {
        return productsCollection.find(new org.bson.Document("url", link));
    }

    public static void updateIndexerCollection(String url, String title,
                                               HashMap<String, Pairing> documentMap, int totalWordCount) {
        /* remove old Document from indexer collection */
        removeFromIndexerCollection(url);

        /* Add the document to the indexer collection */
        for (String word : documentMap.keySet()) {
            float score = (float) Math.log10(documentMap.get(word).value);
            int tf = documentMap.get(word).wordCount;

            indexerCollection.updateOne(
                    Filters.eq("_id", word),
                    new org.bson.Document(
                            "$push",
                            new org.bson.Document(
                                    "documents",
                                    new org.bson.Document("url", url)
                                            .append("score", score)
                                            .append("tf", tf)
                                            .append("word_count", totalWordCount)
                            )
                    ), new UpdateOptions().upsert(true)
            );
        }

        CrawlerCollection.updateOne(Filters.eq("url", url),
                Updates.combine(Updates.set("indexed", true), Updates.set("title", title)));
    }

    public static void removeFromIndexerCollection(String url) {
        indexerCollection.updateMany(new org.bson.Document(),
                Updates.pull("urls", new org.bson.Document("url", url)));

        indexerCollection.deleteMany(Filters.size("urls", 0));
    }

    //////////////////////// TESTING //////////////////////////
    public static void test_unknown() {
        productsCollection.updateOne(Filters.eq("_id", "hello"),

                new org.bson.Document("$push", new org.bson.Document("urls",
                        new org.bson.Document("url", "www.example2.com")
                                .append("score", 4).append("index", 4 - 1))),

                new UpdateOptions().upsert(true));

        CrawlerCollection.updateOne(Filters.eq("_id", "1"),
                Updates.combine(Updates.set("text", "3"), Updates.set("title", "mohamed"), Updates.set("iconUrl", "icoba")),
                new UpdateOptions().upsert(true));
    }
    public static void test_crawler_insert(String url, String html,
                                           String crawled, String indexed) {
        try {
            InsertOneResult result = productsCollection.insertOne(new Document()
                    .append("_id", new ObjectId())
                    .append("url", url)
                    .append("html", html)
                    .append("crawled", crawled)
                    .append(indexed, indexed));
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }
    public static void test_indexer_insert () {
        indexerCollection.updateOne(
                Filters.eq("_id", "hello"),
                new org.bson.Document(
                        "$push",
                        new org.bson.Document(
                                "documents",
                                new org.bson.Document("url", "www.excercise.com")
                                        .append("score", 1.4)
                                        .append("tf", 50)
                                        .append("word_count", 800)
                        )
                ), new UpdateOptions().upsert(true)
        );
    }
    public static void test_insert(String text) {
        InsertOneResult result = productsCollection.insertOne(new Document()
                .append("_id", new ObjectId())
                .append("url", "test_html")
                .append("body", text));

        FindIterable<Document> iterDoc = productsCollection.find(Filters.eq("link", "test.html"));
        org.bson.Document doc = productsCollection.find().first();
        if (doc != null) {
            System.out.println(doc.toJson());
        }
        else {
            System.out.println("Error in printing doc data");
        }
    }

    public static void test_iterable() {
        FindIterable<org.bson.Document> iterable = DBMan.getNotIndexedLinks();
        try {
            org.jsoup.nodes.Document doc = Jsoup.parse(
                    iterable.first().getOrDefault("url", "").toString());
            System.out.println(doc.body().text());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /////////////////////////////////////////////////////////////////

    public static void endConnection() {
        mongoClient.close();
    }

    public static void main(String[] args)  {
        MongoDB database = new MongoDB("webCrawlerDB");

        //database.InsertUrl("url", "html");
        //database.setCrawled("https://en.wikipedia.org/wiki/Main_Page");
        //System.out.println(database.getCrawled().get(0));
        //System.out.println(database.getNotCrawled());

    }
}

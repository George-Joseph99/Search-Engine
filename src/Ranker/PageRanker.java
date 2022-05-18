package Ranker;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.eq;
import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class PageRanker {
    private static final String DB_NAME = "webCrawlerDB"; //idk yet
    private static final String LINKS_COLLECTION_NAME = "CrawlerCollection"; //idk yet
    private static final String HREFS_COLLECTION_NAME = "relationshipsCollection"; //idk yet
    private static final double dampingFactor = 0.85;
    private static final double invDampingFactor = 1 - dampingFactor;
    private static final int numIterations = 1;
    public static void calculatePageRank(MongoDatabase db) throws IOException {
        MongoCollection<Document> linksCollection = db.getCollection(LINKS_COLLECTION_NAME);
        MongoCollection<Document> hrefsCollection = db.getCollection(HREFS_COLLECTION_NAME);
        long noOfLinks = linksCollection.count();
        System.out.println("Number of links: " + noOfLinks);
        double initialPageRank = 1.0 / noOfLinks;
        BasicDBObject resetQuery = new BasicDBObject();
        BasicDBObject resetDocument = new BasicDBObject();
        BasicDBObject resetObject = new BasicDBObject();
        resetDocument.put("PageRank", initialPageRank);
        resetObject.put("$set", resetDocument);
        linksCollection.updateMany(resetQuery, resetObject);
        for (int i = 0; i < numIterations; i++) {
            MongoCursor<Document> linkCursor = linksCollection.find(eq("crawled", "true")).iterator();
            int docCount = 0;
            while (linkCursor.hasNext()) {
                docCount++;
                System.out.println("Doc count: " + docCount);
                ArrayList<URLRank> list = new ArrayList<URLRank>();
                Document current = linkCursor.next();
                ObjectId id = (ObjectId) current.get("_id");
                String str = id.toString();
                MongoCursor<Document> hrefCursor = hrefsCollection.find(eq("parentUrlId",
                        str)).iterator(); // links that refer to my current link
                while (hrefCursor.hasNext()) {
                    Document currentHref = hrefCursor.next();
                    String hrefURL = (String) currentHref.get("href"); // get the link that refers to my current link
                    MongoCursor<Document> linkCounter = linksCollection.find(eq("url", hrefURL)).iterator();
                    // get the number of links that refer to the link that refers to my current link
                    int counter = 0;
                    while (linkCounter.hasNext()) {
                        Document x = linkCounter.next();
                        counter++;
                        System.out.println("Counter: " + counter);
                    }
                    list.add(new URLRank((double) current.get("PageRank"), counter));
                }
                double newPageRank = 0;
                for (URLRank urlRank : list) {
                    newPageRank += urlRank.calculateRank();
                    System.out.println("New page rank: " + newPageRank);
                }
                newPageRank = newPageRank * dampingFactor + invDampingFactor;
                BasicDBObject query = new BasicDBObject();
                query.put("_id", id);
                BasicDBObject document = new BasicDBObject();
                document.put("PageRank", newPageRank);
                BasicDBObject updateObject = new BasicDBObject();
                updateObject.put("$set", document);
                linksCollection.updateOne(query, updateObject);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String uri ="mongodb+srv://nouran:Nouran12345.@cluster0.mg1bc.mongodb.net/webCrawlerDB?retryWrites=true&w=majority";
        ConnectionString connectionString = new ConnectionString(uri);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase db = mongoClient.getDatabase(DB_NAME);
        System.out.println("Connected to DB");
        calculatePageRank(db);
    }
}

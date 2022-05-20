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
    private static final String DB_NAME = "webCrawlerDB"; //crawlerdb
    private static final String LINKS_COLLECTION_NAME = "CrawlerCollection"; //links collection
    private static final String HREFS_COLLECTION_NAME = "relationshipsCollection"; //related links collection
    private static final double dampingFactor = 0.85; //damping factor to visit other network group
    private static final double invDampingFactor = 1 - dampingFactor;
    private static final int numIterations = 1;
    public static void calculatePageRank(MongoDatabase db) throws IOException {
        MongoCollection<Document> linksCollection = db.getCollection(LINKS_COLLECTION_NAME);
        MongoCollection<Document> hrefsCollection = db.getCollection(HREFS_COLLECTION_NAME);
        long noOfLinks = linksCollection.countDocuments();
        System.out.println("Number of links: " + noOfLinks);
        double initialPageRank = 1.0 / noOfLinks;
        BasicDBObject resetQuery = new BasicDBObject();
        BasicDBObject resetDocument = new BasicDBObject();
        BasicDBObject resetObject = new BasicDBObject();
        resetDocument.put("pageRank", initialPageRank); //initialize PageRank to 1/N
        resetObject.put("$set", resetDocument);
        linksCollection.updateMany(resetQuery, resetObject);
        for (int i = 0; i < numIterations; i++) {
            // get cursor to all visited links
            //int docCount = 0;
            MongoCursor<Document> linkCursor = linksCollection.find(eq("crawled", "true")).iterator();
            while (linkCursor.hasNext()) {
                //docCount++;
                //System.out.println("docCount: " + docCount);
                ArrayList<URLRank> list = new ArrayList<URLRank>();
                Document current = linkCursor.next();
                // get the id of each visited link
                ObjectId id = (ObjectId) current.get("_id");
                String str = id.toString();
                // get cursor to links that refer to the current link using the id
                // children urls
                MongoCursor<Document> hrefCursor = hrefsCollection.find(eq("parentUrlId", str)).iterator();
                while (hrefCursor.hasNext()) {
                    Document currentHref = hrefCursor.next();
                    // get the url of each child url
                    String hrefURL = (String) currentHref.get("href");
                    // get cursor to children urls in the links collection
                    MongoCursor<Document> linkCounter = linksCollection.find(eq("url", hrefURL)).iterator();
                    // get the number of children urls
                    int counter = 0;
                    while (linkCounter.hasNext()) {
                        Document x = linkCounter.next();
                        counter++;
                        //System.out.println("Counter: " + counter);
                    }
                    // add each url rank to the list
                    list.add(new URLRank((double) current.get("pageRank"), counter));
                }
                double newPageRank = 0;
                for (URLRank urlRank : list) {
                    // sum the ranks of all children urls
                    newPageRank += urlRank.calculateRank();
                    //System.out.println("New page rank: " + newPageRank);
                }
                // page rank = (1-d) + d*(sum of page ranks of children urls)
                newPageRank = newPageRank * dampingFactor + invDampingFactor;
                // update the page rank of the current link
                BasicDBObject query = new BasicDBObject();
                query.put("_id", id);
                BasicDBObject document = new BasicDBObject();
                document.put("pageRank", newPageRank);
                BasicDBObject updateObject = new BasicDBObject();
                updateObject.put("$set", document);
                linksCollection.updateOne(query, updateObject);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String uri = "mongodb://localhost:27017";
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

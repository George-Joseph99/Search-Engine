package Ranker;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.eq;
import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class PageRanker {
    private static final String DB_NAME = ""; //idk yet
    private static final String LINKS_COLLECTION_NAME = ""; //idk yet
    private static final String HREFS_COLLECTION_NAME = ""; //idk yet
    private static final double dampingFactor = 0.85;
    private static final double invDampingFactor = 1 - dampingFactor;
    private static final int numIterations = 1;
    public static void calculatePageRank(MongoDatabase db) throws IOException {
        MongoCollection<Document> linksCollection = db.getCollection(LINKS_COLLECTION_NAME);
        MongoCollection<Document> hrefsCollection = db.getCollection(HREFS_COLLECTION_NAME);
        long noOfLinks = linksCollection.count();
        double initialPageRank = 1.0 / noOfLinks;
        BasicDBObject resetQuery = new BasicDBObject();
        BasicDBObject resetDocument = new BasicDBObject();
        BasicDBObject resetObject = new BasicDBObject();
        resetDocument.put("PageRank", initialPageRank);
        resetObject.put("$set", resetDocument);
        linksCollection.updateMany(resetQuery, resetObject);
        MongoCursor<Document> holder = linksCollection.find(eq("visited", 1)).iterator();
        for (int i = 0; i < numIterations; i++) {
            MongoCursor<Document> linkCursor = linksCollection.find(eq("visited", 1)).iterator();
            int docCount = 0;
            while (linkCursor.hasNext()) {
                ArrayList<URLRank> list = new ArrayList<URLRank>();
                docCount++;
                Document current = linkCursor.next();
                ObjectId id = (ObjectId) current.get("_id");
                String str = id.toString();
                MongoCursor<Document> hrefCursor = hrefsCollection.find(eq("",
                        str)).iterator(); //links that refer to my link
                while (hrefCursor.hasNext()) {
                    Document currentHref = hrefCursor.next();
                    String hrefCounter = (String) currentHref.get("URL"); //get link that refered to original link
                    MongoCursor<Document> linkCounter = linksCollection.find(eq("URL", hrefCounter)).iterator();
                    int counter = 0;
                    while (linkCounter.hasNext()) {
                        counter++;
                        linkCounter.next();
                    }
                    list.add(new URLRank((double) current.get("PageRank"), counter));
                }
                double newPageRank = 0;
                for (URLRank urlRank : list) {
                    newPageRank += urlRank.calculateRank();
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
}

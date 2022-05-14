package DB;
 
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
//import com.mongodb.client.result.InsertOneResult;

import com.mongodb.MongoException;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;


import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

//"mongodb://localhost1:27017"
//mongodb+srv://nouran:Nouran12345.@cluster0.mg1bc.mongodb.net/myFirstDatabase?retryWrites=true&w=majority
public class MongoDB {
	 MongoCollection<Document> CrawlerCollection;
	
	public MongoDB(String DBname) {
		
		try {
			String uri ="mongodb+srv://nouran:Nouran12345.@cluster0.mg1bc.mongodb.net/webCrawlerDB?retryWrites=true&w=majority";
			ConnectionString connectionString = new ConnectionString(uri);
			
			MongoClientSettings settings = MongoClientSettings.builder()
	                .applyConnectionString(connectionString)
	                .retryWrites(true)
	                .build();
            MongoClient mongoClient = MongoClients.create(settings);
            MongoDatabase database = mongoClient.getDatabase(DBname);
            CrawlerCollection = database.getCollection("CrawlerCollection");
            System.out.println("Connected to DB");

			
		}catch(Exception e) {
            System.out.println("Error connecting to DB");
            e.printStackTrace();
        }	
		
	}
	public void InsertUrl(String url ,String html) {
		try {
	           this.CrawlerCollection.insertOne(new Document()
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
	public void InsertUrl(String url) {
		try {
	           this.CrawlerCollection.insertOne(new Document()
	                    .append("_id", new ObjectId())
	                    .append("url", url)
	                    .append("crawled", "false")
	                    );
	            //System.out.println("Success! Inserted document" );
	        } catch (MongoException me) {
	            System.err.println("Unable to insert due to an error: " +me);
	        }
		
	}
	public void setCrawled(String url) {
		
		Bson updates = Updates.combine(Updates.set("crawled", "true"));
		Document query = new Document().append("url",  url);
		try {
			
			this.CrawlerCollection.updateOne(query, updates);
	            //System.out.println("Success! updated document" );
	        } catch (MongoException me) {
	            System.err.println("Unable to update due to an error: " +me);
	        }
		
	}
public void setContent(String url ,String content) {
		
		Bson updates = Updates.combine(Updates.set("html", content));
		Document query = new Document().append("url",  url);
		try {
			
			this.CrawlerCollection.updateOne(query, updates);
	            //System.out.println("Success! updated document" );
	        } catch (MongoException me) {
	            System.err.println("Unable to update due to an error: " +me);
	        }
		
	}
public  ArrayList<String> getURL(String url) {
	FindIterable <Document> iterable = this.CrawlerCollection.find(new org.bson.Document("url", url))
			.projection(Projections.include("url"));
    List <Document> results = new ArrayList<>();
    iterable.into(results);
    ArrayList<String> matchedUrls = new ArrayList<>();
    for (int i=0 ; i<results.size(); i++) {
    	matchedUrls.add(results.get(i).get("url").toString());
    }
    return matchedUrls;
}
	public  ArrayList<String> getCrawled() {
			FindIterable <Document> iterable = this.CrawlerCollection.find(new org.bson.Document("crawled", "true"))
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
		FindIterable <Document> iterable = this.CrawlerCollection.find(new org.bson.Document("crawled", "false"))
				.projection(Projections.include("url"));
        List <Document> results = new ArrayList<>();
        iterable.into(results);
        ArrayList<String> crawledUrls = new ArrayList<>();
        for (int i=0 ; i<results.size(); i++) {
        	crawledUrls.add(results.get(i).get("url").toString());
        }
        return crawledUrls;
    }

	public static void main(String[] args)  {
		MongoDB database = new MongoDB("webCrawlerDB");
		
		//database.InsertUrl("url", "html");
		//database.setCrawled("https://en.wikipedia.org/wiki/Main_Page");
		//System.out.println(database.getCrawled().get(0));
		//System.out.println(database.getNotCrawled());

	}

}

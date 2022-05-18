package DB;

import com.mongodb.client.*;
//import com.mongodb.client.result.InsertOneResult;

import com.mongodb.MongoException;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import indexer.Pairing;
import org.bson.types.ObjectId;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Aggregates.project;

//"mongodb://localhost1:27017"
//mongodb+srv://nouran:Nouran12345.@cluster0.mg1bc.mongodb.net/myFirstDatabase?retryWrites=true&w=majority
public class MongoDB {
	MongoCollection<Document> CrawlerCollection;
	MongoCollection<Document> relationshipsCollection;
	MongoCollection<Document> indexerCollection;
	MongoCollection<Document> documentCollection;

	public MongoDB(String DBname) {

		try {
			String uri ="mongodb+srv://nouran:Nouran12345.@cluster0.mg1bc.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
			ConnectionString connectionString = new ConnectionString(uri);

			MongoClientSettings settings = MongoClientSettings.builder()
					.applyConnectionString(connectionString)
					.retryWrites(true)
					.build();
			MongoClient mongoClient = MongoClients.create(settings);
			MongoDatabase database = mongoClient.getDatabase(DBname);
			CrawlerCollection = database.getCollection("CrawlerCollection");
			relationshipsCollection = database.getCollection("relationshipsCollection");
			indexerCollection = database.getCollection("IndexerCollection");
			documentCollection = database.getCollection("DocumentCollection");
			System.out.println("Connected to DB");


		}catch(Exception e) {
			System.out.println("Error connecting to DB");
			e.printStackTrace();
		}

	}

	/////////////////////////////////////////////////////////////////
	////////////////////////     CRAWLER     ////////////////////////
	/////////////////////////////////////////////////////////////////

	public void InsertUrl(String url ,String html) {
		try {
			//ArrayList<String> pageLinks = new ArrayList<>();
			this.CrawlerCollection.insertOne(new Document()
							.append("_id", new ObjectId())
							.append("url", url)
							.append("crawled", "false")
							.append("html", html)
							.append("indexed", "false")
					// .append("pageLinks",pageLinks)
			);
			//System.out.println("Success! Inserted document" );
		} catch (MongoException me) {
			System.err.println("Unable to insert due to an error: " +me);
		}

	}
	public void InsertUrl(String url) {
		try {
			//ArrayList<String> pageLinks = new ArrayList<>();
			this.CrawlerCollection.insertOne(new Document()
							.append("_id", new ObjectId())
							.append("url", url)
							.append("crawled", "false")
							.append("indexed", "false")
					//   .append("pageLinks",pageLinks)
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

	public void appendToPagelinks(String url, String href) {


		Document query = new Document().append("url",  url);
		try {

			this.CrawlerCollection.updateOne(query, Updates.addToSet("pageLinks", href));
			//System.out.println("Success! updated document" );
		} catch (MongoException me) {
			System.err.println("Unable to update due to an error: " +me);
		}

	}
	public void setContent(String url ,String content) {

		Bson updates = Updates.combine(Updates.set("html", content),Updates.set("indexed", "false"));
		Document query = new Document().append("url",  url);
		try {

			this.CrawlerCollection.updateOne(query, updates);
			//System.out.println("Success! updated document" );
		} catch (MongoException me) {
			System.err.println("Unable to update due to an error: " +me);
		}

	}
	public  List <Document> getURL(String url) {
		FindIterable <Document> iterable = this.CrawlerCollection.find(new org.bson.Document("url", url));
		//.projection(Projections.include("url"));
		List <Document> results = new ArrayList<>();
		iterable.into(results);
//   System.out.print(results);
//    ArrayList<String> matchedUrls = new ArrayList<>();
//    for (int i=0 ; i<results.size(); i++) {
//    	matchedUrls.add(results.get(i).get("url").toString());
//    }
		return results;
	}
	public  ArrayList<String> getUrlId(String url) {
		FindIterable <Document> iterable = this.CrawlerCollection.find(new org.bson.Document("url", url))
				.projection(Projections.include("url"));
		List <Document> results = new ArrayList<>();
		iterable.into(results);
		ArrayList<String> matchedUrls = new ArrayList<>();
		for (int i=0 ; i<results.size(); i++) {
			matchedUrls.add(results.get(i).get("_id").toString());
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
	public  void removeURL(String url) {
		try {
			this.CrawlerCollection.deleteMany(new org.bson.Document("url", url));
		}catch(MongoException me) {
			System.err.println("Unable to update due to an error: " +me);
		}


	}
	public  void removeLink(String url) {
		try {
			this.relationshipsCollection.deleteMany(new org.bson.Document("url", url));
		}catch(MongoException me) {
			System.err.println("Unable to update due to an error: " +me);
		}


	}
	public void InsertLink(String parentUrlId,String href) {
		try {

			this.relationshipsCollection.insertOne(new Document()
					.append("_id", new ObjectId())
					.append("href", href)
					.append("parentUrlId", parentUrlId)
			);
			//System.out.println("Success! Inserted document" );
		} catch (MongoException me) {
			System.err.println("Unable to insert due to an error: " +me);
		}

	}

	public static void main(String[] args)  {
		MongoDB database = new MongoDB("webCrawlerDB");

		//database.InsertUrl("url", "html");
		//database.appendToPagelinks("https://www.geeksforgeeks.org/", "xxxxx");
		//database.setCrawled("https://en.wikipedia.org/wiki/Main_Page");
		//database.removeURL("https://edition.cnn.com//");
		database.getURL("https://editi.cnn.com//");
		//System.out.println(database.getCrawled().get(0));
		//System.out.println(database.getUrlId("https://www.geeksforgeeks.org/"));

	}

	/////////////////////////////////////////////////////////////////
	////////////////////////     INDEXER     ////////////////////////
	/////////////////////////////////////////////////////////////////

	public FindIterable<org.bson.Document> getNonIndexedLinks() {
		return this.CrawlerCollection.find(
				Filters.and(
						Filters.eq("crawled", "true"),
						Filters.eq("indexed", "false")
				)
		);
	}

	public List<String> getNonIndexedURLS() {
		FindIterable<org.bson.Document> iterable = this.getNonIndexedLinks();
		List<String> urls = new ArrayList<String>();

		for (org.bson.Document doc : iterable) {
			urls.add(doc.getOrDefault("url",null).toString());
		}
		return urls;
	}

	public FindIterable<org.bson.Document> getOneNotIndexedLink(String link) {
		return this.indexerCollection.find(new org.bson.Document("url", link));
	}

	public void updateIndexerCollection(String url, String title, String body,
											   HashMap<String, Pairing> documentMap, int totalWordCount) {
		/* remove old Document from indexer collection */
		this.removeFromIndexerCollection(url);

		/* Add the document to the indexer collection */
		for (String word : documentMap.keySet()) {
			float score = (float) Math.log10(documentMap.get(word).value);
			int tf = documentMap.get(word).wordCount;

			this.indexerCollection.updateOne(
					Filters.eq("_id", word),
					new org.bson.Document(
							"$push",
							new org.bson.Document(
									"documents",
									new org.bson.Document("url", url)
											.append("score", score)
											.append("tf", tf)
							)
					), new UpdateOptions().upsert(true)
			);
		}

		/* Add HTML document content to DocumentCollection */
		this.documentCollection.updateOne(
				Filters.eq("url", url),
				Updates.combine(
						Updates.set("title", title),
						Updates.set("body", body),
						Updates.set("word_count", totalWordCount)
				), new UpdateOptions().upsert(true)
		);

		/* update HTML document in CrawlerCollection */
		this.CrawlerCollection.updateOne(
				Filters.eq("url", url),
				Updates.set("indexed", "true")
		);
	}

	public void removeFromIndexerCollection(String url) {
		this.indexerCollection.updateMany(new org.bson.Document(),
				Updates.pull("documents", new org.bson.Document("url", url)));

		this.indexerCollection.deleteMany(Filters.size("documents", 0));
	}

	public AggregateIterable<Document> getMatchedDocuments (String word) {
		org.bson.conversions.Bson arg11 = match(Filters.eq("_id", word));
		org.bson.conversions.Bson arg12 = unwind("$documents");
		org.bson.conversions.Bson arg13 = project(
				Projections.fields(
						Projections.computed("url", "$documents.url"),
						Projections.computed("score", "$documents.score"),
						Projections.computed("tf", "$documents.tf")
				)
		);
		org.bson.conversions.Bson arg21 = lookup("DocumentCollection", "url", "url", "requested_docs");
		org.bson.conversions.Bson arg22 = unwind("$requested_docs");
		org.bson.conversions.Bson arg23 = project(
				Projections.fields(
						Projections.include("url", "score", "tf"),
						Projections.computed("title", "$requested_docs.title"),
						Projections.computed("body", "$requested_docs.body"),
						Projections.computed("word_count", "$requested_docs.word_count")
				)
		);

		List<org.bson.conversions.Bson> aggrArg = Arrays.asList(
				arg11, arg12, arg13,
				arg21, arg22, arg23
		);
		return this.indexerCollection.aggregate(aggrArg);
	}

	public int countCrawledDocuments() {
		org.bson.conversions.Bson query = Filters.eq("indexed", "true");
		return (int) this.CrawlerCollection.countDocuments(query);
	}


}
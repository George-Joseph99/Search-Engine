package Backend;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet("/Interface")
public class Interface extends HttpServlet {
    private static MongoDatabase database;
    private static MongoClient mongoClient;
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*
        mongoClient = new MongoClient(); //index table
        database = mongoClient.getDatabase("indexDb"); // index database
        MongoCollection<Document> collection = database.getCollection("index_table"); // index collection
        */
        String name = request.getParameter("Name");
        String gender = request.getParameter("Gender");
        String message = "Your name is " + name + " gender is: " + gender;
        response.setContentType("text/html");
        String page = "<!doctype html> <html> <body> <h1> " + message + "</h1> </body> </html>";
        response.getWriter().println(page);
        //queryprocessor qp = new queryprocessor();
        //ArrayList<Words> docs;
       // if (input != null) {
            //String[] final_stemmed_array =
            //docs = qp.non_phrase_search(input);
            // relevanceranker rr = new relevanceranker();
            // rr.calculate_relevance(docs);
            //response.setContentType("text/html");
            //PrintWriter out = response.getWriter();
           // }
        }
    }

    
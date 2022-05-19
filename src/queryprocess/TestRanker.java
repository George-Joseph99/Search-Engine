package queryprocess;
import DB.MongoDB;
import frontend.ResultDisplay;
import utilities.Constants;
import java.util.List;
public class TestRanker {
    public static void main(String[] args) {
        MongoDB dbManager = new MongoDB(Constants.DATABASE_NAME);
        List<RetrievedDocument> docs;
        QueryProcessing qp = new QueryProcessing(dbManager);
        String query = "play";
        docs = qp.processTextQuery(query);
        //System.out.println("doc list size: " + docs.size());
        //ResultDisplay.displayDocuments(docs, 1.0);
        RelevanceRanker rr = new RelevanceRanker();
        rr.CalculateRelevance(docs);
        ResultDisplay.displayDocuments(docs, 1.0);


    }
}

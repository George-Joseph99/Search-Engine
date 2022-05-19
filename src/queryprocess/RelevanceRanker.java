package queryprocess;
import java.util.ArrayList;
import java.util.Collections;
@SuppressWarnings("all")
public class RelevanceRanker {
    public void CalculateRelevance(ArrayList<RetrievedDocument> docList, String query) {
        for (RetrievedDocument doc : docList) {
            doc.calculateTotalRank();
        }
        Collections.sort(docList, Collections.reverseOrder());
    }
}

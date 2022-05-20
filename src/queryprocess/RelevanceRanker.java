package queryprocess;
import java.util.List;
import java.util.Collections;
@SuppressWarnings("all")
public class RelevanceRanker {
    public void CalculateRelevance(List<RetrievedDocument> docList) {
        for (RetrievedDocument doc : docList) {
            doc.calculateTotalRank();
        }
        Collections.sort(docList, Collections.reverseOrder());
    }
}

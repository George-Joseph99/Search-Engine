package queryprocess;

@SuppressWarnings("all")
public class RetrievedDocument implements Comparable<RetrievedDocument> {
    public String url;
    public String title;
    public String body;
    public int tf;
    public double score;
    public int word_count;
    public double tf_idf;
    private double pageRank;
    private double relevanceRank;
    private double totalRank;
    private double idf;

    public RetrievedDocument(String url, String title,
                             String body, int tf, double idf,
                             double score, int word_count, double pageRank) {
        this.url = url;
        this.title = title;
        this.body = body;
        this.tf = tf;
        this.score = score;
        this.word_count = word_count;
        this.tf_idf = -1;
        this.pageRank = pageRank;
        this.idf = idf;
    }

    public void calculateTotalRank() {
        double tf_normalized = (double) this.tf / (double) this.word_count;
        double tfidf = tf_normalized * this.idf;
        this.relevanceRank = this.score + tfidf;
        this.totalRank = this.pageRank + this.relevanceRank;
    }

    public double getTotalRank() {
        return this.totalRank;
    }

    @Override
    public int compareTo(RetrievedDocument o) {
        return Double.compare(this.getTotalRank(), o.getTotalRank());
    }
}

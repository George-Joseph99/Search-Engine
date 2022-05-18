package queryprocess;

public class RetrievedDocument {
    public String url;
    public String title;
    public String body;
    public int tf;
    public double score;
    public int word_count;
    public double tf_idf;

    public RetrievedDocument(String url, String title,
                             String body, int tf,
                             double score, int word_count) {
        this.url = url;
        this.title = title;
        this.body = body;
        this.tf = tf;
        this.score = score;
        this.word_count = word_count;
        this.tf_idf = -1;
    }
}

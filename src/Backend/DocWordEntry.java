@SuppressWarnings("all")
public class DocWordEntry implements Comparable<DocWordEntry> {
    private double pageRank;
    private double relevanceRank;
    private double totalRank;
    private String url;
    private String title;
    private int wordFrequency;
    private double idf;
    private double tf;
    private boolean isInTitle;
    private boolean isInHeader;
    private boolean isInUrl;
    //takes value of 3 if word is in url, 2 if word is in title and 1 if word is in header
    private double wordInUrl;
    private double wordInTitle;
    private double wordInHeader;

    public DocWordEntry(String url, String title, int wordFrequency, double idf, double tf,
                        double pageRank, boolean isInTitle, boolean isInHeader, boolean isInUrl) {
        this.url = url;
        this.title = title;
        this.wordFrequency = wordFrequency;
        this.idf = idf;
        this.tf = tf;
        this.pageRank = pageRank;
        this.isInTitle = isInTitle;
        this.isInHeader = isInHeader;
        this.isInUrl = isInUrl;
        // if word is in url, title or header, set value to 3, 2 or 1 respectively
        this.wordInUrl = this.isInUrl ? 3 : 0;
        this.wordInTitle = this.isInTitle ? 2 : 0;
        this.wordInHeader = this.isInHeader ? 1 : 0;
    }

    // setter and getter
    public double getPageRank() {
        return pageRank;
    }

    public void setPageRank(double pageRank) {
        this.pageRank = pageRank;
    }

    public double getRelevanceRank() {
        return relevanceRank;
    }

    public void setRelevanceRank(double relevanceRank) {
        this.relevanceRank = relevanceRank;
    }

    public double getTotalRank() {
        return totalRank;
    }

    public void setTotalRank(double totalRank) {
        this.totalRank = totalRank;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWordFrequency() {
        return wordFrequency;
    }

    public void setWordFrequency(int wordFrequency) {
        this.wordFrequency = wordFrequency;
    }

    public double getIdf() {
        return idf;
    }

    public void setIdf(double idf) {
        this.idf = idf;
    }

    public double getTf() {
        return tf;
    }

    public void setTf(double tf) {
        this.tf = tf;
    }

    public boolean isInTitle() {
        return isInTitle;
    }

    public void setInTitle(boolean inTitle) {
        isInTitle = inTitle;
    }

    public boolean isInHeader() {
        return isInHeader;
    }

    public void setInHeader(boolean inHeader) {
        isInHeader = inHeader;
    }

    public boolean isInUrl() {
        return isInUrl;
    }

    public void setInUrl(boolean inUrl) {
        isInUrl = inUrl;
    }

    public double getWordInUrl() {
        return wordInUrl;
    }

    public void setWordInUrl(double wordInUrl) {
        this.wordInUrl = wordInUrl;
    }

    public double getWordInTitle() {
        return wordInTitle;
    }

    public void setWordInTitle(double wordInTitle) {
        this.wordInTitle = wordInTitle;
    }

    public double getWordInHeader() {
        return wordInHeader;
    }

    public void setWordInHeader(double wordInHeader) {
        this.wordInHeader = wordInHeader;
    }

    public void calculateTotalRank() {
        double tfidf = this.tf * this.idf;
        this.relevanceRank = this.wordInUrl + this.wordInTitle + this.wordInHeader + tfidf;
        this.totalRank = this.pageRank + this.relevanceRank;
    }

    @Override
    public int compareTo(DocWordEntry o) {
        return Double.compare(this.getTotalRank(), o.getTotalRank());
    }
}



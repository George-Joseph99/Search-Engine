package Ranker;

public class URLRank {
    private double pageRank;
    private int noOfOutOfBoundLinks;

    public URLRank(double pageRank, int noOfOutOfBoundLinks) {
        this.pageRank = pageRank;
        this.noOfOutOfBoundLinks = noOfOutOfBoundLinks;
    }

    public double getPageRank() {
        return pageRank;
    }

    public void setPageRank(double pageRank) {
        this.pageRank = pageRank;
    }

    public int getNoOfOutOfBoundLinks() {
        return noOfOutOfBoundLinks;
    }

    public void setNoOfOutOfBoundLinks(int noOfOutOfBoundLinks) {
        this.noOfOutOfBoundLinks = noOfOutOfBoundLinks;
    }

    public double calculateRank() {
        return (pageRank / noOfOutOfBoundLinks);
    }
}

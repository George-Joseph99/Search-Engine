package indexer;

public class Pairing {
    public int wordCount;
    public long value;
    public boolean isAnotherTag;
    public Pairing(int w, long v) {
        wordCount = w;
        value = v;
        isAnotherTag = false;
    }
}

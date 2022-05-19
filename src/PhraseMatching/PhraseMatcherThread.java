package PhraseMatching;

import queryprocess.RetrievedDocument;
import utilities.WordHelper;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PhraseMatcherThread implements Runnable{
    private int start_index;
    private int end_index;
    private List<RetrievedDocument> retrievedDocuments;
    private ConcurrentHashMap<String, Integer> isFoundMap;
    private StringBruteforce sb;

    public PhraseMatcherThread(int s, int e, List<RetrievedDocument> rd, ConcurrentHashMap<String, Integer> is_found, StringBruteforce sb) {
        start_index = s;
        end_index = e;
        retrievedDocuments = rd;
        isFoundMap = is_found;
        this.sb = sb;
    }
    public void PhraseMatcherRun(RetrievedDocument document) {
        String str = document.title + " " + document.body;
        List<String> convertedStr = WordHelper.convertString(str);
        if (sb.bruteforceMatch(convertedStr)) {
            isFoundMap.put(document.url, 1);
        }
    }
    @Override
    public void run() {
        if (end_index != -1 && retrievedDocuments.size() > 0)
        {
            for (int i = start_index; i <= end_index; i++) {
                PhraseMatcherRun(retrievedDocuments.get(i));
            }
        }
    }
}

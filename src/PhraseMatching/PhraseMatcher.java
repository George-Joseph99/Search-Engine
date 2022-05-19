package PhraseMatching;

import indexer.IndexerThread;
import queryprocess.RetrievedDocument;
import utilities.WordHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PhraseMatcher {
    public static final int THREAD_NUMBER = 10;

    public static List<RetrievedDocument> matchPhrase(List<RetrievedDocument> retrievedDocuments,
                                                      String query)
    {
        ConcurrentHashMap<String, Integer> isFoundMap = new ConcurrentHashMap<>();
        List<String> convertedQuery = WordHelper.processString(query);
        List<RetrievedDocument> resultDocuments = new ArrayList<RetrievedDocument>();
        KMPStringMatching kmp = new KMPStringMatching(convertedQuery);
        List<Thread> threads = new ArrayList<Thread>();

        int DOC_PER_THREAD = Double.valueOf(Math.ceil((double) retrievedDocuments.size()
                / (double) THREAD_NUMBER)).intValue();
        List<Integer> threadSegments = WordHelper.calculateLinkSegments(THREAD_NUMBER, retrievedDocuments.size());

        int start_index = 0;
        int end_index = 0;
        for(int i = 0; i < THREAD_NUMBER; i++) {
            if (i == 0) {
                start_index = 0;
                end_index = threadSegments.get(i) - 1;
            }
            else {
                start_index += threadSegments.get(i);
                end_index += threadSegments.get(i);
            }

            //do threads
            Thread thread;
            if (threadSegments.get(i) > 0)
                thread = new Thread(new PhraseMatcherThread(start_index,
                        end_index,retrievedDocuments, isFoundMap, kmp ));
            else
                thread = new Thread(new PhraseMatcherThread(-1, -1,
                        retrievedDocuments, isFoundMap, kmp ));
            thread.start();
            threads.add(thread);

        }

        for(int i = 0; i < THREAD_NUMBER; i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (RetrievedDocument document : retrievedDocuments) {
            if (isFoundMap.get(document.url) != null) {
                resultDocuments.add(document);
            }
        }

        return resultDocuments;
    }
}

package indexer;

import DB.MongoDB;
import utilities.Constants;
import utilities.WordHelper;

import java.util.ArrayList;
import java.util.List;

public class Indexer{
    private final static int THREAD_NUMBER = 10;

    public static void runIndexer(MongoDB dbman) {
        MongoDB dbManager;
            if (dbman == null) {
                dbManager = new MongoDB(Constants.DATABASE_NAME);
            } else {
                dbManager = dbman;
            }
        List<String> urls = dbManager.getNonIndexedURLS();
        List<Thread> threads = new ArrayList<Thread>();

        int LINKS_PER_THREAD = Double.valueOf(Math.ceil((double) urls.size()
                / (double) THREAD_NUMBER)).intValue();
        List<Integer> threadSegments = WordHelper.calculateLinkSegments(THREAD_NUMBER, urls.size());

        int start_index = 0;
        int end_index = 0;
        for (int i = 0; i < THREAD_NUMBER; i++) {
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
                 thread = new Thread(new IndexerThread(dbManager,
                         start_index, end_index, urls));
            else
                thread = new Thread(new IndexerThread(dbManager, -1, -1, urls));
            thread.start();
            threads.add(thread);
        }

        for (int i = 0; i < THREAD_NUMBER; i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //urls.clear();
        //threads.clear();
    }
    public static void main(String[] args ) {
        MongoDB dbManger = new MongoDB(Constants.DATABASE_NAME);

        /* Indexer test */
        dbManger.dropIndexCollections();
        dbManger.resetCrawlerIndexed();

        double startTime  = (double)System.nanoTime();
        Indexer.runIndexer(dbManger);
        double endTime  = (double)System.nanoTime();

        double totalTime = (endTime - startTime)* (1e-9);
        System.out.println("indexer total time: " + totalTime);
    }
}

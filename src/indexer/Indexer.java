package indexer;

import DB.MongoDB;
import utilities.Constants;
import utilities.WordHelper;

import java.util.ArrayList;
import java.util.List;

public class Indexer{
    private final int THREAD_NUMBER = 10;
    MongoDB dbManager;
    public List<String> urls;
    private List<Thread> threads = new ArrayList<Thread>();
    public Indexer(MongoDB dbman) {
        if (dbman == null) {
            dbManager = new MongoDB(Constants.DATABASE_NAME);
        } else {
            dbManager = dbman;
        }
        urls = dbManager.getNonIndexedURLS();
    }
    public void runIndexer() {

        int LINKS_PER_THREAD = Double.valueOf(Math.ceil((double) this.urls.size()
                / (double) this.THREAD_NUMBER)).intValue();
        List<Integer> threadSegments = WordHelper.calculateLinkSegments(this.THREAD_NUMBER, this.urls.size());
        //List<Integer> threadSegments = calculateLinkSegments(this.THREAD_NUMBER, 100);
        //IndexerThread it = new IndexerThread(dbManager, 0, urls.size() - 1, urls);
        //it.run();

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



    /*
    public void run() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     */
}

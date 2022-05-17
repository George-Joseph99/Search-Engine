package indexer;

import DB.DBMan;
import com.mongodb.client.FindIterable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.ceil;

public class Indexer {
    private static final int THREAD_NUMBER = 10;
    public static List<String> urls = DBMan.getNotIndexedURLS();
    private static List<Thread> threads = new ArrayList<Thread>();

    public static void runIndexer() {
        int LINKS_PER_THREAD = Double.valueOf(Math.ceil((double) urls.size()
                / (double) THREAD_NUMBER)).intValue();
        List<Integer> threadSegments = calculateLinkSegments(THREAD_NUMBER, urls.size());

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
                 thread = new Thread(new IndexerThread(start_index, end_index, urls));
            else
                thread = new Thread(new IndexerThread(-1, -1, urls));
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

    }

    private static List<Integer> calculateLinkSegments (int thread_number, int total_number) {
        int docs_per_thread = Double.valueOf(Math.ceil((double) total_number
                / (double) thread_number)).intValue();

        List<Integer> list = new ArrayList<Integer>();
        int current_number = total_number;

        while(current_number > 0) {
            if (current_number > docs_per_thread) {
                list.add(docs_per_thread);
                current_number -= docs_per_thread;
            }
            else {
                list.add(current_number);
                current_number = 0;
            }
        }

        while (thread_number > list.size()) {
            list.add(-1);
        }
        return list;
    }
}
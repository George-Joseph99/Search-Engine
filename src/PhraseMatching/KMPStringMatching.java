package PhraseMatching;

import java.util.*;
// JAVA program for implementation of KMP pattern
// searching algorithm
class KMPStringMatching {
    private int[] lps;
    List<String> pat;

    public KMPStringMatching(List<String> p) {
        this.pat = p;
        lps = new int[p.size()];
    }

    public boolean KMPSearch(List<String> txt) {
        int M = pat.size();
        int N = txt.size();

        int j = 0; // index for pat[]

        // Preprocess the pattern (calculate lps[]
        // array)

        //computeLPSArray();

        int i = 0; // index for txt[]
        while (i < N) {
            if (pat.get(j).equals(txt.get(i))) {
                j++;
                i++;
            }
            if (j == M) {
                return true;
            }

            // mismatch after j matches
            else if (i < N && pat.get(j) != txt.get(i)) {
                // Do not match lps[0..lps[j-1]] characters,
                // they will match anyway
                if (j != 0)
                    j = lps[j - 1];
                else
                    i = i + 1;
            }
        }
        return false;
    }

    void computeLPSArray() {
        // length of the previous longest prefix suffix
        int len = 0;
        int i = 1;
        lps[0] = 0; // lps[0] is always 0

        // the loop calculates lps[i] for i = 1 to M-1
        while (i < pat.size()) {
            if (pat.get(i).equals(pat.get(len))) {
                len++;
                lps[i] = len;
                i++;
            } else // (pat[i] != pat[len])
            {
                // This is tricky. Consider the example.
                // AAACAAAA and i = 7. The idea is similar
                // to search step.
                if (len != 0) {
                    len = lps[len - 1];

                    // Also, note that we do not increment
                    // i here
                } else // if (len == 0)
                {
                    lps[i] = len;
                    i++;
                }
            }
        }
    }
}
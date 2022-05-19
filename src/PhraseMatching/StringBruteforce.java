package PhraseMatching;

import java.util.List;

public class StringBruteforce {
    List<String> pat;

    public StringBruteforce(List<String> p) {
        pat = p;
    }

    public boolean bruteforceMatch(List<String> txt) {
        int n = txt.size();
        int m = pat.size();
        for (int i = 0; i < n - m; i++) {
            int j = 0;
            while (j < m && txt.get(i+j).equals(pat.get(j))) {
                j++;
            }
            if (j == m) return true;
        }
        return false;
    }
}

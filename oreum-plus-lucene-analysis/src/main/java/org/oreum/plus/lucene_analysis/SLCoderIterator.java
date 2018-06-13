package org.oreum.plus.lucene_analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A BreakIterator-like API for iterating over subwords in text, according to CoderFilter rules.
 *
 * @lucene.internal
 */
public final class SLCoderIterator extends CoderIterator {

    /**
     * Create a new SLCoderIterator operating with the supplied rules.
     */
    SLCoderIterator() {
    }

    Iterator<String> split(String s) {
        List<String> res = new ArrayList<>();

        if ("".equals(s) || s == null) return res.iterator();

        String[] splits = s.split("-");

        for (int i = 0; i < splits.length; i++) {
            res.add(splits[i]);
        }

        if (splits.length > 2) {
            for (int i = 0; i < splits.length - 1; i++) {
                res.add(splits[i] + "-" + splits[i + 1]);
                res.add(splits[i] + splits[i + 1]);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splits.length; i++) {
            sb.append(splits[i]);
        }

        res.add(sb.toString());

        return res.iterator();
    }

}

package org.oreum.plus.lucene_analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A BreakIterator-like API for iterating over subwords in text, according to FLCoderFilter rules.
 *
 * @lucene.internal
 */
public final class FLCoderIterator extends CoderIterator {

    /**
     * Create a new WordDelimiterIterator operating with the supplied rules.
     */
    FLCoderIterator() {
    }

    Iterator<String> split(String s) {
        List<String> res = new ArrayList<>();

        if ("".equals(s) || s == null) return res.iterator();

        String[] splits = s.split("-");

        for (int i = 0; i < splits.length; i++) {
            if (i > 0) res.add("-");
            res.add(splits[i]);
        }

        return res.iterator();
    }

}

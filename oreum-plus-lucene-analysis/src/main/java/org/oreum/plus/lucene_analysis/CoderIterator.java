package org.oreum.plus.lucene_analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A BreakIterator-like API for iterating over subwords in text, according to WordDelimiterGraphFilter rules.
 *
 * @lucene.internal
 */
public class CoderIterator {

    String text;
    Iterator<String> it;

    /**
     * Create a new WordDelimiterIterator operating with the supplied rules.
     */
    CoderIterator() {
        setText(null);
    }

    /**
     * Advance to the next subword in the string.
     *
     * @return index of the next subword, or if all subwords have been returned
     */
    String next() {
        return it.next();
    }

    boolean hasNext() {
        return it.hasNext();
    }

    /**
     * Reset the text to a new value, and reset all state
     *
     * @param text New text
     */
    void setText(String text) {
        this.text = text;
        this.it = split(text);
    }


    Iterator<String> split(String s) {
        List<String> res = new ArrayList<>();

        if ("".equals(s) || s == null) return res.iterator();

        String[] splits = s.split("-");

        for (int i = 0; i < splits.length; i++) {
            if (i > 0) res.add("-");
            res.add(splits[i]);
        }

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

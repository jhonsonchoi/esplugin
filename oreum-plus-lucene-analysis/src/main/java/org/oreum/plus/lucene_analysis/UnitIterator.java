package org.oreum.plus.lucene_analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A BreakIterator-like API for iterating over subwords in text, according to WordDelimiterGraphFilter rules.
 * @lucene.internal
 */
public final class UnitIterator {

    String text;
    Iterator<String> it;

    /**
     * Create a new WordDelimiterIterator operating with the supplied rules.
     */
    UnitIterator() {
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
    void setText(String text, String postfix) {
        this.text = text;
        this.it = split(text, postfix);
    }

    Iterator<String> split(String s, String postfix) {
        List<String> res = new ArrayList<>();

        res.add(s.substring(0, s.length() - postfix.length()));
        res.add(postfix);

        return res.iterator();
    }

}

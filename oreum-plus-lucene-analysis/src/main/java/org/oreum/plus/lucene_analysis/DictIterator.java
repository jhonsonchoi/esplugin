package org.oreum.plus.lucene_analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A BreakIterator-like API for iterating over subwords in text, according to WordDelimiterGraphFilter rules.
 * @lucene.internal
 */
public final class DictIterator {

    String text;
    Iterator<String> it;

    /**
     * Create a new WordDelimiterIterator operating with the supplied rules.
     */
    DictIterator() {
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
    void setText(String text, String prefix) {
        this.text = text;
        this.it = split(text, prefix);
    }

    Iterator<String> split(String s, String prefix) {
        List<String> res = new ArrayList<>();

        res.add(prefix);
        res.add(s.substring(prefix.length()));

        return res.iterator();
    }

}

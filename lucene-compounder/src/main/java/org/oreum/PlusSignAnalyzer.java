package org.oreum;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

import java.io.Reader;

public class PlusSignAnalyzer extends Analyzer {
    /* This is the only function that we need to override for our analyzer. It takes in a
     * java.io.Reader object and saves the tokenizer and list of token filters that operate
     * on it.
     */
    protected TokenStreamComponents createComponents(String s) {
        Tokenizer tokenizer = new PlusSignTokenizer();
        TokenStream filter = new EmptyStringTokenFilter(tokenizer);
        filter = new LowerCaseFilter(filter);
        return new Analyzer.TokenStreamComponents(tokenizer, filter);
    }
}

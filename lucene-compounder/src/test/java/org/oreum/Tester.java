package org.oreum;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

public class Tester extends Analyzer {

    private final CharArraySet words;

    public Tester() {
        Set<String> s = new HashSet<>();
        s.add("onetwo");
        s.add("twothree");
        words = new CharArraySet(s, false);

//        System.out.println(words.size());
    }


    @Override
    protected TokenStreamComponents createComponents(String s) {
//        System.out.println(s);
        final Tokenizer source = new WhitespaceTokenizer();
        TokenStream result = new CompounderFilter(source, words);
        return new TokenStreamComponents(source, result);
    }

    public static void main(String[] args) throws IOException {

        Analyzer analyzer = new Tester();
        TokenStream ts = analyzer.tokenStream("myfield", new StringReader("some test goes here one two three four five end"));
//        OffsetAttribute offsetAtt = ts.addAttribute(OffsetAttribute.class);
        CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);

        try {
            ts.reset();
            while (ts.incrementToken()) {
//                System.out.println("token" + ts.reflectAsString(true));

                System.out.println(termAtt.toString());
/*
                System.out.println("token start offset: " + offsetAtt.startOffset());
                System.out.println("  token end offset: " + offsetAtt.endOffset());
*/
            }
            ts.end();
        } finally {
            ts.close();
        }
    }
}

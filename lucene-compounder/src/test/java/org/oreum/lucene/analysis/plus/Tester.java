package org.oreum.lucene.analysis.plus;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.tokenattributes.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

public class Tester extends Analyzer {

    private final CharArraySet words;

    private final byte[] charTypeTable;
    private final int flags;
    private final CharArraySet protoWords;


    public Tester() {
        Set<String> s = new HashSet<>();
        s.add("onetwo");
        s.add("twothree");
        words = new CharArraySet(s, false);

        charTypeTable = DelimiterIterator.DEFAULT_WORD_DELIM_TABLE;
        flags = DelimiterFilter.GENERATE_WORD_PARTS
                | DelimiterFilter.GENERATE_NUMBER_PARTS
                | DelimiterFilter.SPLIT_ON_CASE_CHANGE
                | DelimiterFilter.PRESERVE_ORIGINAL
                | DelimiterFilter.SPLIT_ON_NUMERICS
                | DelimiterFilter.STEM_ENGLISH_POSSESSIVE;
        protoWords = null;

//        System.out.println(words.size());
    }


/*
    @Override
    protected TokenStreamComponents createComponents(String s) {
        final Tokenizer source = new WhitespaceTokenizer();
        TokenStream result = new CompounderFilter(source, words);
        return new TokenStreamComponents(source, result);
    }
*/

    protected TokenStreamComponents createComponents(String s) {
        final Tokenizer source = new WhitespaceTokenizer();
        TokenStream result = new DelimiterFilter(source, charTypeTable, flags, protoWords);
        return new TokenStreamComponents(source, result);
    }

    public static void main(String[] args) throws IOException {

        Analyzer analyzer = new Tester();
        TokenStream ts = analyzer.tokenStream("myfield", new StringReader("start -R- RH9SG end"));
//        OffsetAttribute offsetAtt = ts.addAttribute(OffsetAttribute.class);
        CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAtt = ts.addAttribute(OffsetAttribute.class);
        PositionIncrementAttribute posIncAtt = ts.addAttribute(PositionIncrementAttribute.class);
        TypeAttribute typeAttribute = ts.addAttribute(TypeAttribute.class);

        int position = 0;

        try {
            ts.reset();
            while (ts.incrementToken()) {
//                System.out.println("token" + ts.reflectAsString(true));

                System.out.println(termAtt.toString());
                System.out.println("token start offset: " + offsetAtt.startOffset());
                System.out.println("  token end offset: " + offsetAtt.endOffset());
                System.out.println("    token position: " + position);
                System.out.println("        token type: " + typeAttribute.type());

                position += posIncAtt.getPositionIncrement();
            }
            ts.end();
        } finally {
            ts.close();
        }
    }
}

package org.oreum.plus.lucene_analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.io.StringReader;

public class CoderTester extends Analyzer {

    private final byte[] charTypeTable;
    private final int flags;
    private final CharArraySet protoWords;

    public CoderTester() {
        charTypeTable = DelimiterIterator.DEFAULT_WORD_DELIM_TABLE;
        flags = DelimiterFilter.GENERATE_WORD_PARTS
                | DelimiterFilter.GENERATE_NUMBER_PARTS
                | DelimiterFilter.SPLIT_ON_CASE_CHANGE
                | DelimiterFilter.PRESERVE_ORIGINAL
                | DelimiterFilter.SPLIT_ON_NUMERICS
                | DelimiterFilter.STEM_ENGLISH_POSSESSIVE;
        protoWords = null;
    }

    protected TokenStreamComponents createComponents(String s) {
        final Tokenizer source = new WhitespaceTokenizer();
        TokenStream result = new OffsetFilter(new DelimiterFilter(new CoderFilter(source), charTypeTable, flags, protoWords));
        return new TokenStreamComponents(source, result);
    }

    public static void main(String[] args) throws IOException {

        Analyzer analyzer = new CoderTester();
//        TokenStream ts = analyzer.tokenStream("myfield", new StringReader("pre post"));
        TokenStream ts = analyzer.tokenStream("myfield", new StringReader("pre nt900x5t-x716a-zz10 post"));

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
                System.out.print("\t  offset: " + offsetAtt.startOffset() + "," + offsetAtt.endOffset());
                System.out.print("\tposition: " + position);
                System.out.print("\t    type: " + typeAttribute.type());
                System.out.println();

                position += posIncAtt.getPositionIncrement();
            }
            ts.end();
        } finally {
            ts.close();
        }
    }
}

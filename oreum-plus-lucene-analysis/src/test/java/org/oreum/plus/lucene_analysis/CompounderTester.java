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
import java.util.HashSet;
import java.util.Set;

public class CompounderTester extends Analyzer {

    private final CharArraySet words;

    public CompounderTester() {
        Set<String> s = new HashSet<>();
        s.add("onetwo");
        s.add("onetwothree");
        s.add("threefour");
        s.add("sixseven");
        s.add("남성구스다운");
        s.add("구스다운");
        s.add("일반세탁기");
        words = new CharArraySet(s, false);
    }


    @Override
    protected TokenStreamComponents createComponents(String s) {
        final Tokenizer source = new WhitespaceTokenizer();
        TokenStream result = new CompounderFilter(source, words, false);
        return new TokenStreamComponents(source, result);
    }

    public static void main(String[] args) throws IOException {

        Analyzer analyzer = new CompounderTester();
        TokenStream ts = analyzer.tokenStream("myfield", new StringReader("zero one two three four five six seven 남성 구스 다운 세탁기 일반 세탁기"));
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

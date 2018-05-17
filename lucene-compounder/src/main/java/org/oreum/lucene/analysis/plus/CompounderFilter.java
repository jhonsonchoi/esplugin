package org.oreum.lucene.analysis.plus;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;

public final class CompounderFilter extends TokenFilter {
    private final CharArraySet words;
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posAttr = addAttribute(PositionIncrementAttribute.class);
    private final OffsetAttribute offAtt = addAttribute(OffsetAttribute.class);
    private State state;
    private StringBuilder pre;
    private boolean consumed;

    public CompounderFilter(TokenStream input, CharArraySet words) {
        super(input);
        this.words = words;
    }

    @Override
    public boolean incrementToken() throws IOException {

        while (input.incrementToken()) {
//            System.out.println("token: " + new StringBuilder().append(termAtt.buffer(), 0 , termAtt.length()).toString());
/*
            System.out.println(pre.toString());
            System.out.println(termAtt.buffer());
            System.out.println(termAtt.length());
*/

            if (pre == null) {
                pre = new StringBuilder();
                pre.append(termAtt.buffer(), 0, termAtt.length());
                consumed = false;
            } else {
                StringBuilder sb = new StringBuilder(pre.toString());

                sb.append(termAtt.buffer(), 0, termAtt.length());

//                System.out.println("*");

                if (words.contains(sb.toString())) {

                    String s = sb.toString();

                    pre = new StringBuilder();
                    pre.append(termAtt.buffer(), 0, termAtt.length());
                    consumed = true;


                    termAtt.setEmpty();
                    termAtt.append(s);

                    offAtt.setOffset(0,0);

                    return true;
                } else {

                    if (!consumed) {
                        String s = pre.toString();

                        pre = new StringBuilder();
                        pre.append(termAtt.buffer(), 0, termAtt.length());
                        consumed = false;

                        termAtt.setEmpty();
                        termAtt.append(s);

                        offAtt.setOffset(0,0);

                        return true;
                    } else {
                        pre = new StringBuilder();
                        pre.append(termAtt.buffer(), 0, termAtt.length());
                        consumed = false;
                    }
                }
            }
//            termAtt.setEmpty();
        }

        if (pre.length() > 0 && !consumed) {
            termAtt.setEmpty();
            termAtt.append(pre.toString());
            consumed = true;

            offAtt.setOffset(0,0);

            return true;
        }

        return false;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        pre = null;
        consumed = false;
    }

    @Override
    public void end() throws IOException {
        super.end();
    }
}

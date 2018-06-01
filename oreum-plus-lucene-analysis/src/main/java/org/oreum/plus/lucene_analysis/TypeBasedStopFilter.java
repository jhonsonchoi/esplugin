package org.oreum.plus.lucene_analysis;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;

public final class TypeBasedStopFilter extends TokenFilter {

    private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAttribute = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncAttribute = addAttribute(PositionIncrementAttribute.class);
    private final TypeAttribute typeAttribute = addAttribute(TypeAttribute.class);

    public TypeBasedStopFilter(TokenStream in) {
        super(in);
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (true) {
            // 업스트림에 토큰이 더이상 없으면 끝
            if (!input.incrementToken()) {
                return false;
            }

            // 업스트림에 토큰이 있는 경우

            if (typeAttribute.type().equals("XSN")) continue;

            return true;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
    }

    @Override
    public void end() throws IOException {
        super.end();
    }
}

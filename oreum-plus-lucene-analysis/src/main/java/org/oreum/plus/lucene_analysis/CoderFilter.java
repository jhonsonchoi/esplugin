package org.oreum.plus.lucene_analysis;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;

public final class CoderFilter extends TokenFilter {

    private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAttribute = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncAttribute = addAttribute(PositionIncrementAttribute.class);
    private final TypeAttribute typeAttribute = addAttribute(TypeAttribute.class);

    // used for iterating word delimiter breaks
    private final CoderIterator flIterator;
    private final CoderIterator slIterator;

    private boolean hasSavedState = false;

    public CoderFilter(TokenStream in) {
        super(in);
        this.flIterator = new FLCoderIterator();
        this.slIterator = new SLCoderIterator();
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (true) {
            if (!hasSavedState) {
                // 업스트림에 토큰이 더이상 없으면 끝
                if (!input.incrementToken()) {
                    if (slIterator.hasNext()) {
                        String t = slIterator.next();

                        termAttribute.setEmpty();
                        termAttribute.append(t);

                        typeAttribute.setType("mariner");

                        return true;
                    }
                    return false;
                }

                // 업스트림에 토큰이 있는 경우

                int termLength = termAttribute.length();
                char[] termBuffer = termAttribute.buffer();

                // - 가 포함된 코드 패턴인가 검사해서 포함되지 않은 경우는 그대로 리턴

                String sb = new StringBuilder().append(termBuffer, 0, termLength).toString();
                if (sb.indexOf('-') < 0) {
                    return true;
                }

                // - 가 포함된 경우

                flIterator.setText(termAttribute.toString());
                slIterator.setText(termAttribute.toString());

                if (!flIterator.hasNext()) {
                    continue;
                }

                // offset, type, term 저장
                hasSavedState = true;
            }

            // at the end of the string, tryOutput any concatenations
            if (!flIterator.hasNext()) {
                hasSavedState = false;
                continue;
            }

            String t = flIterator.next();

            termAttribute.setEmpty();
            termAttribute.append(t);

            return true;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        hasSavedState = false;
    }

    @Override
    public void end() throws IOException {
        super.end();
    }
}

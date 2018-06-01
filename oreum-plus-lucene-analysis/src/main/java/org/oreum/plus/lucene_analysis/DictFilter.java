package org.oreum.plus.lucene_analysis;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.util.Iterator;

public final class DictFilter extends TokenFilter {
    private final CharArraySet words;
    private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

    // used for iterating word delimiter breaks
    private final DictIterator iterator;

    private boolean hasSavedState = false;

    public DictFilter(TokenStream input, CharArraySet words) {
        super(input);
        this.words = words;
        this.iterator = new DictIterator();
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (true) {
            if (!hasSavedState) {
                // 업스트림에 토큰이 더이상 없으면 끝
                if (!input.incrementToken()) {
                    return false;
                }

                // 업스트림에 토큰이 있는 경우

                int termLength = termAttribute.length();
                char[] termBuffer = termAttribute.buffer();

                // - 가 포함된 코드 패턴인가 검사해서 포함되지 않은 경우는 그대로 리턴

                String sb = new StringBuilder().append(termBuffer, 0, termLength).toString();

                boolean in = false;
                String prefix = null;
                Iterator wordsIt = words.iterator();

                while (wordsIt.hasNext()) {
                    prefix = new StringBuilder().append((char[]) wordsIt.next()).toString();

                    if (sb.startsWith(prefix)) {
                        in = true;
                        break;
                    }
                }

                if (!in) {
                    return true;
                }

                // - 가 포함된 경우

                iterator.setText(termAttribute.toString(), prefix);

                if (!iterator.hasNext()) {
                    continue;
                }

                // offset, type, term 저장
                hasSavedState = true;
            }

            // at the end of the string, tryOutput any concatenations
            if (!iterator.hasNext()) {
                hasSavedState = false;
                continue;
            }

            String t = iterator.next();

            termAttribute.setEmpty();
            termAttribute.append(t);

            return true;
        }
    }

    @Override
    public void end() throws IOException {
        super.end();
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        hasSavedState = false;
    }
}

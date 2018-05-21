package org.oreum.plus.lucene.analysis;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.util.Arrays;

public final class CompounderFilter extends TokenFilter {
    private final CharArraySet words;
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
    private State state;
    private boolean nomore;
    private int max = 3;

    public CompounderFilter(TokenStream input, CharArraySet words) {
        super(input);
        this.words = words;
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (true) {
            if (nomore) {
                if (bufferedLen > 0) { // exist
                    if (tryOutput()) { // compound
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                if (bufferedLen < max) {
                    if (input.incrementToken()) {
                        buffer();
                    } else {
                        nomore = true;
                    }
                } else {
                    if (tryOutput()) { // compound
                        return true;
                    }
                }
            }
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        bufferedLen = 0;
        nomore = false;
    }

    @Override
    public void end() throws IOException {
        super.end();
    }

    private AttributeSource.State buffered[] = new AttributeSource.State[8];
    private int startOff[] = new int[8];
    private int posInc[] = new int[8];
    private int bufferedLen = 0;
    private String term[] = new String[8];
    private boolean isCompoundPart[] = new boolean[8];
    private boolean printed[] = new boolean[8];

    private void buffer() {
        if (bufferedLen == buffered.length) {
            int newSize = ArrayUtil.oversize(bufferedLen+1, 8);
            buffered = Arrays.copyOf(buffered, newSize);
            startOff = Arrays.copyOf(startOff, newSize);
            posInc = Arrays.copyOf(posInc, newSize);
            term = Arrays.copyOf(term, newSize);
            isCompoundPart = Arrays.copyOf(isCompoundPart, newSize);
        }
        startOff[bufferedLen] = offsetAtt.startOffset();
        posInc[bufferedLen] = posIncAtt.getPositionIncrement();
        buffered[bufferedLen] = captureState();
        term[bufferedLen] = termAtt.toString();
        isCompoundPart[bufferedLen] = false;
        printed[bufferedLen] = false;
        bufferedLen++;
    }

    private void checkCompound() {
        for (int i = 0; i < bufferedLen; i++) {
            isCompoundPart[i] = false;
        }
        if (bufferedLen > 1) {
            // check compound
            for (int i = bufferedLen; i > 0; i--) {
                String compound = getBufferString(i);

                if (words.contains(compound)) { // exist
                    for (int j = 0; j < i; j++) {
                        isCompoundPart[j] = true;
                    }
                    break;
                }
            }
        }
    }

    String getBufferString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) sb.append(term[i]);
        return sb.toString();
    }

    int compoundLength() {
        int length = 0;
        for (int i = 0; i < bufferedLen; i++) {
            if (isCompoundPart[i]) length++;
        }
        return length;
    }

    int printedLength() {
        int length = 0;
        for (int i = 0; i < bufferedLen; i++) {
            if (printed[i]) length++;
        }
        return length;
    }

    void shift() {
        bufferedLen--;

        for (int i = 0; i < bufferedLen; i++) {
            startOff[i] = startOff[i + 1];
            posInc[i] = posInc[i + 1];
            buffered[i] = buffered[i + 1];
            term[i] = term[i + 1];
            isCompoundPart[i] = isCompoundPart[i + 1];
            printed[i] = printed[i + 1];
        }
    }

    boolean tryOutput() {

        if (bufferedLen == 0) return false; // error

        checkCompound();

        if (!isCompoundPart[0]) {
            if (printed[0]) {
                shift();

                return false;
            } else {
                clearAttributes();
                termAtt.setEmpty();
                termAtt.append(term[0]);

                offsetAtt.setOffset(0, 0);

                shift();

                return true;
            }
        } else {
            if (compoundLength() > printedLength()) {
                clearAttributes();
                termAtt.setEmpty();
                termAtt.append(getBufferString(compoundLength()));

                offsetAtt.setOffset(0, 0);

                for (int i = 0; i < compoundLength(); i++) {
                    printed[i] = true;
                }

                shift();

                return true;
            } else {
                shift();

                return false;
            }
        }
    }

    boolean outputAll() {

        if (bufferedLen == 0) return false; // error
        if (bufferedLen == 1) {
            if (!printed[0]) {
                termAtt.setEmpty();
                termAtt.append(term[0]);

                offsetAtt.setOffset(0, 0);

                bufferedLen = 0;
                return true;
            } else {
                bufferedLen = 0;
                return false;
            }
        }
        if (bufferedLen > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bufferedLen; i++) sb.append(term[i]);

            termAtt.setEmpty();
            termAtt.append(sb.toString());

            offsetAtt.setOffset(0,0);

            bufferedLen = 0;

            return true;
        }

        return false;
    }
}

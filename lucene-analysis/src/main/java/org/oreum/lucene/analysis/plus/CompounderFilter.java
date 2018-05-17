package org.oreum.lucene.analysis.plus;

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

    public CompounderFilter(TokenStream input, CharArraySet words) {
        super(input);
        this.words = words;
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (true) {
            if (nomore) {
                if (bufferedLen > 0) { // exist
                    if (outputAll()) { // compound
                        return true;
                    } else {
                        continue;
                    }
                } else {
                    return false;
                }
            }

            if (bufferedLen < 2) {
                if (input.incrementToken()) {
                    buffer();
                } else {
                    nomore = true;
                }
            } else {
                String compound = getBufferString();

                if (words.contains(compound)) { // exist
                    if (input.incrementToken()) {
                        buffer();
                    } else {
                        nomore = true;
                    }
                } else {
                    // output all - 1
                    if (output()) { // compound
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
    private boolean printed[] = new boolean[8];

    private void buffer() {
        if (bufferedLen == buffered.length) {
            int newSize = ArrayUtil.oversize(bufferedLen+1, 8);
            buffered = Arrays.copyOf(buffered, newSize);
            startOff = Arrays.copyOf(startOff, newSize);
            posInc = Arrays.copyOf(posInc, newSize);
            term = Arrays.copyOf(term, newSize);
            printed = Arrays.copyOf(printed, newSize);
        }
        startOff[bufferedLen] = offsetAtt.startOffset();
        posInc[bufferedLen] = posIncAtt.getPositionIncrement();
        buffered[bufferedLen] = captureState();
        term[bufferedLen] = termAtt.toString();
        printed[bufferedLen] = false;
        bufferedLen++;
    }

    String getBufferString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bufferedLen; i++) sb.append(term[i]);
        return sb.toString();
    }

    boolean output() {

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
        if (bufferedLen == 2) {
            if (printed[0]) {

                startOff[0] = startOff[1];
                posInc[0] = posInc[1];
                buffered[0] = buffered[1];
                term[0] = term[1];
                printed[0] = printed[1];

                bufferedLen = 1;

                return false;
            } else {
                termAtt.setEmpty();
                termAtt.append(term[0]);

                offsetAtt.setOffset(0,0);

                startOff[0] = startOff[1];
                posInc[0] = posInc[1];
                buffered[0] = buffered[1];
                term[0] = term[1];
                printed[0] = printed[1];

                bufferedLen = 1;

                return true;
            }
        }
        if (bufferedLen > 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bufferedLen - 1; i++) sb.append(term[i]);

            termAtt.setEmpty();
            termAtt.append(sb.toString());

            offsetAtt.setOffset(0,0);

            startOff[0] = startOff[bufferedLen - 2];
            posInc[0] = posInc[bufferedLen - 2];
            buffered[0] = buffered[bufferedLen - 2];
            term[0] = term[bufferedLen - 2];
            printed[0] = true;
            startOff[1] = startOff[bufferedLen - 1];
            posInc[1] = posInc[bufferedLen - 1];
            buffered[1] = buffered[bufferedLen - 1];
            term[1] = term[bufferedLen - 1];
            printed[1] = printed[bufferedLen - 1];

            bufferedLen = 2;

            return true;
        }

        return false;
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

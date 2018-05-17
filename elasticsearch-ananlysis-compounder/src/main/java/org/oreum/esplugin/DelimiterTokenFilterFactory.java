package org.oreum.esplugin;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;
import org.oreum.DelimiterFilter;
import org.oreum.DelimiterIterator;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.oreum.DelimiterFilter.*;

public class DelimiterTokenFilterFactory extends AbstractTokenFilterFactory {
    private final byte[] charTypeTable;
    private final int flags;
    private final CharArraySet protoWords;

    DelimiterTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);

        List<String> charTypeTableValues = Analysis.getWordList(env, settings, "type_table");
        if (charTypeTableValues == null) {
            this.charTypeTable = DelimiterIterator.DEFAULT_WORD_DELIM_TABLE;
        } else {
            this.charTypeTable = parseTypes(charTypeTableValues);
        }
        int flags = 0;
        flags |= getFlag(GENERATE_WORD_PARTS, settings, "generate_word_parts", true);
        flags |= getFlag(GENERATE_NUMBER_PARTS, settings, "generate_number_parts", true);
        flags |= getFlag(CATENATE_WORDS, settings, "catenate_words", false);
        flags |= getFlag(CATENATE_NUMBERS, settings, "catenate_numbers", false);
        flags |= getFlag(CATENATE_ALL, settings, "catenate_all", false);
        flags |= getFlag(SPLIT_ON_CASE_CHANGE, settings, "split_on_case_change", true);
        flags |= getFlag(PRESERVE_ORIGINAL, settings, "preserve_original", true);
        flags |= getFlag(SPLIT_ON_NUMERICS, settings, "split_on_nemerics", true);
        flags |= getFlag(STEM_ENGLISH_POSSESSIVE, settings, "stem_english_possessive", true);
        this.flags = flags;
        Set<?> protectedWords = Analysis.getWordSet(env, indexSettings.getIndexVersionCreated(), settings, "protected_words");
        this.protoWords = protectedWords == null ? null : CharArraySet.copy(protectedWords);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new DelimiterFilter(tokenStream, charTypeTable, flags, protoWords);
    }

    public int getFlag(int flag, Settings settings, String key, boolean defaultValue) {
        if (settings.getAsBoolean(key, defaultValue)) {
            return flag;
        }
        return 0;
    }

    static byte[] parseTypes(Collection<String> rules) {
        return null;
    }
}

package org.oreum.plus.elasticsearch_plugin;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;
import org.oreum.plus.lucene_analysis.UnitFilter;

import java.util.List;

public class UnitTokenFilterFactory extends AbstractTokenFilterFactory {
    private final CharArraySet unitWords;
    private static final String UNIT_WORDS_KEY = "unit_words";
    private static final String UNIT_WORDS_PATH_KEY = UNIT_WORDS_KEY + "_path";

    UnitTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);

        final List<String> arrayUnitWords = settings.getAsList(UNIT_WORDS_KEY, null);
        final String unitWordsPath = settings.get(UNIT_WORDS_PATH_KEY, null);
        if (arrayUnitWords == null && unitWordsPath == null || arrayUnitWords != null && unitWordsPath != null) {
            throw new IllegalArgumentException("plus_unit requires either `" + UNIT_WORDS_KEY + "` or `"
                + UNIT_WORDS_PATH_KEY + "` to be configured");
        }
        this.unitWords = Analysis.getWordSet(env, indexSettings.getIndexVersionCreated(), settings, UNIT_WORDS_KEY);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new UnitFilter(tokenStream, unitWords);
    }
}

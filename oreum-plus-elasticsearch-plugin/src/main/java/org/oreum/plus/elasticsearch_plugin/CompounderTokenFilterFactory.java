package org.oreum.plus.elasticsearch_plugin;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;
import org.oreum.plus.lucene_analysis.CompounderFilter;

import java.util.List;

public class CompounderTokenFilterFactory extends AbstractTokenFilterFactory {
    private static final String COMPOUND_WORDS_KEY = "compound_words";
    private static final String COMPOUND_WORDS_PATH_KEY = COMPOUND_WORDS_KEY + "_path";
    private static final String PRESERVE_ORIGINAL = "preserve_original";

    private final CharArraySet compoundWords;
    private final boolean preserveOriginal;

    CompounderTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);

        final List<String> arrayCompoundWords = settings.getAsList(COMPOUND_WORDS_KEY, null);
        final String compoundWordsPath = settings.get(COMPOUND_WORDS_PATH_KEY, null);
        if (arrayCompoundWords == null && compoundWordsPath == null || arrayCompoundWords != null && compoundWordsPath != null) {
            throw new IllegalArgumentException("compounder requires either `" + COMPOUND_WORDS_KEY + "` or `"
                + COMPOUND_WORDS_PATH_KEY + "` to be configured");
        }
        this.compoundWords = Analysis.getWordSet(env, indexSettings.getIndexVersionCreated(), settings, COMPOUND_WORDS_KEY);
        this.preserveOriginal = settings.getAsBoolean(PRESERVE_ORIGINAL, false);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new CompounderFilter(tokenStream, compoundWords);
    }
}

package org.oreum.plus.elasticsearch_plugin;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;
import org.oreum.plus.lucene_analysis.DictFilter;

import java.util.List;

public class DictTokenFilterFactory extends AbstractTokenFilterFactory {
    private final CharArraySet dictWords;
    private static final String DICT_WORDS_KEY = "dict_words";
    private static final String DICT_WORDS_PATH_KEY = DICT_WORDS_KEY + "_path";

    DictTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);

        final List<String> arrayCompoundWords = settings.getAsList(DICT_WORDS_KEY, null);
        final String compoundWordsPath = settings.get(DICT_WORDS_PATH_KEY, null);
        if (arrayCompoundWords == null && compoundWordsPath == null || arrayCompoundWords != null && compoundWordsPath != null) {
            throw new IllegalArgumentException("plus_dict requires either `" + DICT_WORDS_KEY + "` or `"
                + DICT_WORDS_PATH_KEY + "` to be configured");
        }
        this.dictWords = Analysis.getWordSet(env, indexSettings.getIndexVersionCreated(), settings, DICT_WORDS_KEY);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new DictFilter(tokenStream, dictWords);
    }
}

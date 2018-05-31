package org.oreum.plus.elasticsearch_plugin;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.oreum.plus.lucene_analysis.CoderFilter;

public class CoderTokenFilterFactory extends AbstractTokenFilterFactory {

    CoderTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new CoderFilter(tokenStream);
    }
}

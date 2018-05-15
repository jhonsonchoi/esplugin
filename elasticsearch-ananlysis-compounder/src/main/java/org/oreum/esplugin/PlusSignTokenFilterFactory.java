package org.oreum.esplugin;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexSettings;
import  org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.oreum.EmptyStringTokenFilter;
import org.oreum.PlusSignAnalyzer;

import java.io.IOException;

public class PlusSignTokenFilterFactory extends AbstractTokenFilterFactory {

    public PlusSignTokenFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
    }

    public TokenStream create(TokenStream tokenStream) {
        return new EmptyStringTokenFilter(tokenStream);
    }
}

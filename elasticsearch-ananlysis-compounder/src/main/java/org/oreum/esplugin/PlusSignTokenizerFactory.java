package org.oreum.esplugin;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.oreum.PlusSignTokenizer;

public class PlusSignTokenizerFactory  extends AbstractTokenizerFactory {
    public PlusSignTokenizerFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
    }

    @Override
    public Tokenizer create() {
        return new PlusSignTokenizer();
    }
}

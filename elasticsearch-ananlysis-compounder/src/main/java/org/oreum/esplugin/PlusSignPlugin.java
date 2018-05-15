package org.oreum.esplugin;

import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.Map;
import java.util.TreeMap;

import static org.elasticsearch.plugins.AnalysisPlugin.requriesAnalysisSettings;

public class PlusSignPlugin extends Plugin implements AnalysisPlugin {

    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        Map<String, AnalysisProvider<TokenFilterFactory>> filters = new TreeMap<>();
        filters.put("xxx_tf", PlusSignTokenFilterFactory::new);
        filters.put("oreum", requriesAnalysisSettings(CompounderTokenFilterFactory::new));
        return filters;
    }

    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        Map<String, AnalysisProvider<TokenizerFactory>> tokenizers = new TreeMap<>();
        tokenizers.put("xxx_t", PlusSignTokenizerFactory::new);
        return tokenizers;
    }
}

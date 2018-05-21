package org.oreum.plus.elasticsearch_plugin;

import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.Map;
import java.util.TreeMap;

import static org.elasticsearch.plugins.AnalysisPlugin.requriesAnalysisSettings;

public class PlusPlugin extends Plugin implements AnalysisPlugin {

    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        Map<String, AnalysisProvider<TokenFilterFactory>> filters = new TreeMap<>();
        filters.put("catenator", requriesAnalysisSettings(CompounderTokenFilterFactory::new));
        filters.put("delimiter", DelimiterTokenFilterFactory::new);
        return filters;
    }
}
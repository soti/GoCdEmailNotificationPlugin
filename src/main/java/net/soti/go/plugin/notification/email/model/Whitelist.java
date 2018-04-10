package net.soti.go.plugin.notification.email.model;

/**
 * User: wsim
 * Date: 2018-04-10
 */
public class Whitelist {
    private static final String SPLITTER = "/";
    private final String pipeline;
    private final String stage;
    private final SearchFilterTypes pipelineSearchFilterType;
    private final SearchFilterTypes stageSearchFilterType;

    private Whitelist(String pipeline, String stage) {
        pipelineSearchFilterType = SearchFilterTypes.findType(pipeline);
        stageSearchFilterType = SearchFilterTypes.findType(stage);
        this.pipeline = SearchFilterTypes.getKeyword(pipeline);
        this.stage = SearchFilterTypes.getKeyword(stage);
    }

    public static Whitelist getWhitelistItem(String whitelistItem) {
        if (!whitelistItem.contains(SPLITTER)) {
            return null;
        }

        String[] items = whitelistItem.trim().split(SPLITTER);

        if (items.length != 2) {
            return null;
        }

        Whitelist result = new Whitelist(items[0], items[1]);

        if(result.getStageSearchFilterType().equals(SearchFilterTypes.Unknown)
                || result.getPipelineSearchFilterType().equals(SearchFilterTypes.Unknown)) {
            return null;
        }

        return result;
    }

    @Override
    public String toString(){
        return String.format("%s/%s (%s/%s)", pipeline, stage, pipelineSearchFilterType.name(), stageSearchFilterType.name());
    }

    public boolean isWhitelisted(String pipelineName, String stageName) {
        if (pipelineName == null || pipelineName.length() == 0) {
            throw new IllegalArgumentException("Pipeline name must be specified.");
        }
        if (stageName == null || stageName.length() == 0) {
            throw new IllegalArgumentException("Stage name must be specified.");
        }
        if (getPipelineSearchFilterType().equals(SearchFilterTypes.Unknown) || getStageSearchFilterType().equals(SearchFilterTypes.Unknown)) {
            throw new IllegalStateException("Search criteria of the whitelist item is Unknown.");
        }

        return validate(pipelineName, getPipelineSearchFilterType(), getPipelineName())
                && validate(stageName, getStageSearchFilterType(), getStageName());
    }

    private boolean validate(String value, SearchFilterTypes criteria, String key){
        switch (criteria) {
            case FullText:
                return key.equals(value);
            case StartsWith:
                return value.startsWith(key);
            case EndsWith:
                return value.endsWith(key);
            case Contains:
                return value.contains(key);
            case None:
            default:
                return true;
        }
    }

    private String getPipelineName() {
        return pipeline;
    }

    private String getStageName() {
        return stage;
    }

    private SearchFilterTypes getPipelineSearchFilterType() {
        return pipelineSearchFilterType;
    }

    private SearchFilterTypes getStageSearchFilterType() {
        return stageSearchFilterType;
    }
}

package net.soti.go.plugin.notification.email.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: wsim
 * Date: 2018-04-05
 */
public class PipelineRevision {
    private static final Pattern PIPELINE_REVISION_PATTERN = Pattern.compile("^([^/]+)/(\\d+)/([^/]+)/(\\d+)$");

    private final String pipelineName;
    private final int pipelineCounter;
    private final String stageName;
    private final int stageCounter;

    public PipelineRevision(String pipelineName, int pipelineCounter, String stageName, int stageCounter) {
        this.pipelineName = pipelineName;
        this.pipelineCounter = pipelineCounter;
        this.stageName = stageName;
        this.stageCounter = stageCounter;
    }

    public static PipelineRevision parseRevision(String revision) {
        Matcher matcher = PIPELINE_REVISION_PATTERN.matcher(revision);

        if (!matcher.matches()) {
            return null;
        }

        return new PipelineRevision(
                matcher.group(1),
                Integer.parseInt(matcher.group(2)),
                matcher.group(3),
                Integer.parseInt(matcher.group(4)));
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public int getPipelineCounter() {
        return pipelineCounter;
    }

    public int getStageCounter() {
        return stageCounter;
    }

    public String getStageName() {
        return stageName;
    }
}

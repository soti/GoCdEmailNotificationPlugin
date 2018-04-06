package net.soti.go.plugin.notification.email.model.gocd;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import net.soti.go.plugin.notification.email.model.MaterialType;
import net.soti.go.plugin.notification.email.model.PipelineRevision;
import net.soti.go.plugin.notification.email.utils.GoCdClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.logging.Logger;

/**
 * User: wsim
 * Date: 2018-03-18
 */
public class Pipeline {
    private static final Logger LOG = Logger.getLoggerFor(Pipeline.class);
    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("counter")
    private int counter;
    @Expose
    @SerializedName("build_cause")
    private BuildCause buildCause;
    @Expose
    @SerializedName("stages")
    private Stage[] stages;

    private List<PipelineRevision> upstreamPipelines = null;

    @Override
    public String toString() {
        if (stages != null && stages.length > 0) {
            return name + "/" + counter + "/" + stages[0].getName() + "/" + stages[0].getResult();
        } else {
            return name + "/" + counter;
        }
    }

    public static Pipeline fromJson(String json) {
        return GSON.fromJson(json, Pipeline.class);
    }

    public StageResultType getStageResult(final String stageName) {
        return findStage(stageName).get().getResult();
    }

    public List<PipelineRevision> getRecursiveUpstreamPipelines(final GoCdClient client) throws IOException {
        List<PipelineRevision> result = new ArrayList<>();

        List<PipelineRevision> currentUpstreams = getUpstreamPipelines();
        result.addAll(getUpstreamPipelines());
        for (PipelineRevision revision : currentUpstreams) {
            Pipeline pipeline = client.getPipeline(revision.getPipelineName(), revision.getPipelineCounter());
            result.addAll(pipeline.getRecursiveUpstreamPipelines(client));
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public int getCounter() {
        return counter;
    }

    public Stage[] getStages() {
        return stages;
    }

    public BuildCause getBuildCause() {
        return buildCause;
    }

    private List<PipelineRevision> getUpstreamPipelines() {
        if (upstreamPipelines == null) {
            upstreamPipelines = buildCause.getMaterialRevisions().stream()
                    .filter(revision -> MaterialType.Pipeline.equals(revision.getMaterial().getType()))
                    .flatMap(revision -> revision.getModifications().stream())
                    .map(modification -> PipelineRevision.parseRevision(modification.getRevision()))
                    .collect(Collectors.toList());
        }
        return upstreamPipelines;
    }

    private Optional<Stage> findStage(final String stageName) {
        return Arrays.stream(stages).filter(stage -> stageName.equals(stage.getName())).findFirst();
    }
}

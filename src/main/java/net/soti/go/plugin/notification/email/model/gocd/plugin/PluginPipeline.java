package net.soti.go.plugin.notification.email.model.gocd.plugin;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.soti.go.plugin.notification.email.model.ChangedMaterial;
import net.soti.go.plugin.notification.email.model.MaterialType;
import net.soti.go.plugin.notification.email.model.PipelineRevision;
import net.soti.go.plugin.notification.email.model.gocd.ExecutionResultType;
import net.soti.go.plugin.notification.email.model.gocd.Pipeline;
import net.soti.go.plugin.notification.email.model.gocd.StageResultType;
import net.soti.go.plugin.notification.email.utils.GoCdClient;
import net.soti.go.plugin.notification.email.utils.LdapManager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.logging.Logger;

/**
 * User: wsim
 * Date: 2018-03-18
 */
public class PluginPipeline {
    List<ChangedMaterial> changedMaterials = new ArrayList<>();
    private static final Logger LOG = Logger.getLoggerFor(PluginPipeline.class);
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("counter")
    private int counter;
    @Expose
    @SerializedName("build-cause")
    private PluginBuildCause[] buildCauses;
    @Expose
    @SerializedName("stage")
    private PluginStage stage;
    private ExecutionResultType resultType;
    private boolean isReady = false;

    @Override
    public String toString() {
        return name + "/" + counter + "/" + stage.getName() + "/" + stage.getResult();
    }

    public void initialize(final GoCdClient client, final LdapManager manager) throws IOException {
        StageResultType currentStageResult = stage.getResult();
        LOG.info(String.format("Initialize %s/%d/%s/%d [%s]", name, counter, stage.getName(), stage.getCounter(), currentStageResult.name()));

        switch (currentStageResult) {
            case Passed:
                resultType = ExecutionResultType.Passed;
                if (counter > 1) {
                    Pipeline lastRunPipeline = client.getLastRun(name, counter, stage.getName());
                    if(lastRunPipeline != null && !StageResultType.Passed.equals(lastRunPipeline.getStageResult(stage.getName()))){
                        resultType = ExecutionResultType.Fixed;
                    }
                }
                break;
            case Cancelled:
            case Failed:
                resultType = ExecutionResultType.Broken;
                if (counter > 1) {
                    Pipeline lastRunPipeline = client.getLastRun(name, counter, stage.getName());
                    if(lastRunPipeline != null && !StageResultType.Passed.equals(lastRunPipeline.getStageResult(stage.getName()))){
                        resultType = ExecutionResultType.Failing;
                    }
                }
                break;
            case Unknown:
                resultType = ExecutionResultType.Building;
                break;
            default:
                throw new IOException("Unexpected stage status: " + currentStageResult);
        }

        LOG.info(String.format("'%s/%d/%s/%d' is %s", name, counter, stage.getName(), stage.getCounter(), resultType.name()));

        if (ExecutionResultType.Passed.equals(resultType) || ExecutionResultType.Building.equals(resultType)) {
            isReady = true;
            return;
        }

        changedMaterials.clear();

        int endCounter = resultType.equals(ExecutionResultType.Fixed) ? counter - 1 : counter;
        final List<Pipeline> pipelines = client.getPipelineHistorySinceLastSuccess(name, endCounter, stage.getName());
        Pipeline startPipeline = pipelines.get(pipelines.size() - 1);
        final boolean everRed = !startPipeline.getStageResult(stage.getName()).equals(StageResultType.Passed);
        LOG.info(String.format("'%s/%d/%s' read histories(%d) from counter %d to %d (EverRed:%s).",
                name, counter, stage.getName(), pipelines.size(), startPipeline.getCounter(), endCounter, everRed));

        if (everRed) {
            changedMaterials.addAll(getChangesOfList(pipelines, everRed, manager));
        }

        final List<PipelineRevision> endUpstreams = pipelines.get(0).getRecursiveUpstreamPipelines(client);
        final List<PipelineRevision> startUpstreams = startPipeline.getRecursiveUpstreamPipelines(client);

        final HashMap<String, Integer> endUpstreamMap = new HashMap<>();
        final HashMap<String, Integer> startUpstreamMap = new HashMap<>();

        for (PipelineRevision revision : endUpstreams) {
            if (!endUpstreamMap.containsKey(revision.getPipelineName())) {
                endUpstreamMap.put(revision.getPipelineName(), revision.getPipelineCounter());
            } else {
                if (endUpstreamMap.get(revision.getPipelineName()) < revision.getPipelineCounter()) {
                    endUpstreamMap.put(revision.getPipelineName(), revision.getPipelineCounter());
                }
            }
        }
        for (PipelineRevision revision : startUpstreams) {
            if (!startUpstreamMap.containsKey(revision.getPipelineName())) {
                startUpstreamMap.put(revision.getPipelineName(), revision.getPipelineCounter());
            } else {
                if (startUpstreamMap.get(revision.getPipelineName()) > revision.getPipelineCounter()) {
                    startUpstreamMap.put(revision.getPipelineName(), revision.getPipelineCounter());
                }
            }
        }

        for (Map.Entry<String, Integer> entry : endUpstreamMap.entrySet()) {
            int firstCounter = everRed ? 1 : startUpstreamMap.getOrDefault(entry.getKey(), 1);
            int lastCounter = entry.getValue();
            if (!everRed && entry.getValue() == firstCounter) {
                continue;
            }
            firstCounter += 1;

            List<Pipeline> upstreamHistory = client.getPipeineHistory(entry.getKey(), firstCounter, lastCounter);
            changedMaterials.addAll(getChangesOfList(upstreamHistory, everRed, manager));
        }

        isReady = true;
    }

    public List<ChangedMaterial> getAllChangedMaterials() {
        if (!isReady) {
            throw new RuntimeException("Pipeline data has not been initialized.");
        }
        return changedMaterials;
    }

    public String getName() {
        return name;
    }

    public int getCounter() {
        return counter;
    }

    public PluginBuildCause[] getBuildCauses() {
        return buildCauses;
    }

    public PluginStage getStage() {
        return stage;
    }

    public StageResultType getStageResult() {
        return stage.getResult();
    }

    public ExecutionResultType getExecutionResultType() {
        if (!isReady) {
            throw new RuntimeException("Pipeline data has not been initialized.");
        }
        return resultType;
    }

    private List<ChangedMaterial> getChangesOfList(final List<Pipeline> pipelines, final boolean getAll, final LdapManager manager) {
        List<ChangedMaterial> result = new ArrayList<>();
        for (Pipeline pipeline : pipelines) {
            pipeline.getBuildCause().getMaterialRevisions().stream()
                    .filter(revision -> (getAll || revision.isChanged()) && !MaterialType.Pipeline.equals(revision.getMaterial().getType()))
                    .map(revision -> revision.getChangedMaterials(
                            manager,
                            pipeline.getName(),
                            pipeline.getCounter(),
                            pipeline.getStages().get(0).getName(),
                            pipeline.getStages().get(0).getCounter()))
                    .forEach(result::addAll);
        }

        return result;
    }
}

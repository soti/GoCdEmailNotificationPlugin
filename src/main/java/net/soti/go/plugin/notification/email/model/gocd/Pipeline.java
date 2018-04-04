package net.soti.go.plugin.notification.email.model.gocd;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("counter")
    public int counter;

    @Expose
    @SerializedName("build_cause")
    public BuildCause buildCause;

    @Expose
    @SerializedName("stages")
    public Stage[] stages;

    private static final Logger LOG = Logger.getLoggerFor(Pipeline.class);

    private static final Gson GSON = new GsonBuilder().
            excludeFieldsWithoutExposeAnnotation().
            create();
    private boolean keepFailing = false;

    @Override
    public String toString() {
        if (stages != null && stages.length > 0) {
            return name + "/" + counter + "/" + stages[0].name + "/" + stages[0].result;
        } else {
            return name + "/" + counter;
        }
    }

    public static Pipeline fromJson(String json) {
        return GSON.fromJson(json, Pipeline.class);
    }

    public List<MaterialRevision> rootChanges(GoCdClient client, String stageName) throws IOException {
        final List<MaterialRevision> result = new ArrayList<>();
        try {
            final List<Pipeline> history = client.getPipelineHistorySinceLastSuccess(name, String.valueOf(counter), stageName);
            if (history.size() > 1) {
                keepFailing = true;
            }

            for (Pipeline pipeline : history) {
                List<MaterialRevision> changes = getChangesInternal(client, pipeline);
                result.addAll(changes);
            }

            return result;
        } catch (Exception e) {
            LOG.error("Failure in getChanges:", e);
            throw e;
        }
    }

    public List<MaterialRevision> subChanges(GoCdClient client) throws IOException {
        final List<MaterialRevision> result = new ArrayList<>();
        try {
            Pipeline pipeline = client.getPipeline(name, counter);
            List<MaterialRevision> changes = getChangesInternal(client, pipeline);
            result.addAll(changes);

            return result;
        } catch (Exception e) {
            LOG.error("Failure in getChanges:", e);
            throw e;
        }
    }

    public boolean isKeepFailing() {
        return keepFailing;
    }

    private List<MaterialRevision> getChangesInternal(GoCdClient client, Pipeline pipeline) {
        ArrayList result = new ArrayList();

        List<MaterialRevision> revisions = Arrays.stream(pipeline.buildCause.materialRevisions)
                .filter(mr -> mr.changed).collect(Collectors.toList());
        revisions.stream().filter(mr -> !mr.material.isPipeline())
                .forEach(result::add);

        revisions.stream().filter(MaterialRevision::isPipeline)
                .map(mr -> {
                    try {
                        return mr.getRecurseChanges(client);
                    } catch (IOException e) {
                        LOG.error("Failed to read recursive change.", e);
                    }
                    return new ArrayList();
                })
                .forEach(result::addAll);

        return result;
    }
}

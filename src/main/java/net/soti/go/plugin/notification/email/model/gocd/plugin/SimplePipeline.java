package net.soti.go.plugin.notification.email.model.gocd.plugin;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.soti.go.plugin.notification.email.model.ChangedMaterial;
import net.soti.go.plugin.notification.email.model.PipelineMaterial;
import net.soti.go.plugin.notification.email.model.gocd.MaterialRevision;
import net.soti.go.plugin.notification.email.model.gocd.Pipeline;
import net.soti.go.plugin.notification.email.utils.GoCdClient;
import net.soti.go.plugin.notification.email.utils.LdapManager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.logging.Logger;

/**
 * User: wsim
 * Date: 2018-03-18
 */
public class SimplePipeline {
    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("counter")
    public int counter;

    @Expose
    @SerializedName("build-cause")
    public BuildCause[] buildCauses;

    @Expose
    @SerializedName("stage")
    public SimpleStage stage;

    private static final Logger LOG = Logger.getLoggerFor(SimplePipeline.class);

    private boolean keepFailing = false;

    @Override
    public String toString() {
        return name + "/" + counter + "/" + stage.name + "/" + stage.result;
    }

    public boolean isKeepFailing() {
        return keepFailing;
    }

    public List<ChangedMaterial> rootChanges(final GoCdClient client, final LdapManager manager) throws IOException {
        final List<MaterialRevision> result = new ArrayList<>();
        if (stage.result.equals(ResultType.Failed) || stage.result.equals(ResultType.Cancelled)) {
            Pipeline pipeline = new Pipeline();
            pipeline.name = name;
            pipeline.counter = counter;
            result.addAll(pipeline.rootChanges(client, stage.name));
            keepFailing = pipeline.isKeepFailing();
        }
        return result.stream().flatMap(mr -> mr.getChangedMaterials(manager).stream()).collect(Collectors.toList());
    }

    public StageStateType getChangedState() {
        return stage.state;
    }

    private PipelineMaterial[] getUpstreamPipelines() {
        return (PipelineMaterial[]) Arrays.stream(buildCauses)
                .map(BuildCause::getPipelineMaterial)
                .filter(Objects::nonNull)
                .toArray();
    }
}

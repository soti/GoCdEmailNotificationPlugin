package net.soti.go.plugin.notification.email.model.gocd;

import java.util.List;
import java.util.stream.Collectors;

import net.soti.go.plugin.notification.email.model.ChangedMaterial;
import net.soti.go.plugin.notification.email.utils.LdapManager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.logging.Logger;

/**
 * User: wsim
 * Date: 2018-03-18
 */
public class MaterialRevision {
    private static final Logger LOG = Logger.getLoggerFor(MaterialRevision.class);
    @Expose
    @SerializedName("changed")
    private boolean changed;
    @Expose
    @SerializedName("material")
    private Material material;
    @Expose
    @SerializedName("modifications")
    private List<Modification> modifications;

    public String modificationUrl(Modification modification) {
        String url = material.getBaseUrl();
        switch (material.getType()) {
            case Git:
            case Tfs:
                return String.format("%s/%s", url, modification.getRevision());
            case Package:
                return url;
            default:
                return null;
        }
    }

    public List<ChangedMaterial> getChangedMaterials(final LdapManager manager, final String pipelineName, final int pipelineCounter,
                                                     final String stageName, final int stageCounter) {
        return modifications.stream().map(mod ->
                new ChangedMaterial(mod.getUserName(),
                        mod.getEmail(),
                        modificationUrl(mod),
                        material.getType(),
                        material.getName(),
                        mod.getRevision(),
                        mod.getComment(),
                        mod.getTime(),
                        manager,
                        pipelineName,
                        pipelineCounter,
                        stageName,
                        stageCounter)
        ).collect(Collectors.toList());
    }

    public boolean isChanged() {
        return changed;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isPipeline() {
        return material.isPipeline();
    }

    public List<Modification> getModifications() {
        return modifications;
    }
}
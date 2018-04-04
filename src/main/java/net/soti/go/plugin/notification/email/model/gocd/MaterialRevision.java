package net.soti.go.plugin.notification.email.model.gocd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.soti.go.plugin.notification.email.model.ChangedMaterial;
import net.soti.go.plugin.notification.email.model.MaterialType;
import net.soti.go.plugin.notification.email.utils.GoCdClient;
import net.soti.go.plugin.notification.email.utils.LdapManager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.logging.Logger;

/**
 * User: wsim
 * Date: 2018-03-18
 */
public class MaterialRevision {
    @Expose
    @SerializedName("changed")
    public boolean changed;

    @Expose
    @SerializedName("material")
    public Material material;

    @Expose
    @SerializedName("modifications")
    public List<Modification> modifications;

    static private final Pattern PIPELINE_REVISION_PATTERN = Pattern.compile("^([^/]+)/(\\d+)/([^/]+)/(\\d+)$");
    private static final Logger LOG = Logger.getLoggerFor(MaterialRevision.class);

    public String modificationUrl(Modification modification) {
        if ((!material.type.equals(MaterialType.Git) && !material.type.equals(MaterialType.Tfs))
                || material.description == null || modification.revision == null) {
            LOG.warn(String.format("Failed to generate changeset URL for modification (%s)/(%s)/(%s)",
                    material.type, material.description,
                    modification.revision));
            return null;
        }

        String url = material.getBaseUrl();
        switch (material.type) {
            case Git:
            case Tfs:
                return String.format("%s/%s", url, modification.revision);
            case Package:
                return url;
            default:
                LOG.warn(String.format("Failed to generate changeset URL for modification (%s)/(%s)/(%s)",
                        material.type, material.description,
                        modification.revision));
                return null;
        }
    }

    public List<MaterialRevision> getRecurseChanges(GoCdClient client) throws IOException {
        List<MaterialRevision> result = new ArrayList<>();
        if (!isPipeline()) {
            return result;
        }

        Matcher matcher = PIPELINE_REVISION_PATTERN.matcher(modifications.get(0).revision);
        List<Pipeline> pipelines = client.getPipelineHistorySinceLastSuccess(matcher.group(1), matcher.group(2), matcher.group(3));

        for (Pipeline pipeline : pipelines) {
            result.addAll(pipeline.rootChanges(client, matcher.group(3)));
        }

        return result;
    }

    public List<ChangedMaterial> getChangedMaterials(final LdapManager manager) {
        return modifications.stream().map(mod -> {
            String email = mod.email;
            String user = mod.userName;
            String link = modificationUrl(mod);
            return new ChangedMaterial(user, email, link, material.type, material.getName(), mod.revision, mod.comment, manager);
        }).collect(Collectors.toList());
    }

    public boolean isPipeline() {
        return material.isPipeline();
    }
}
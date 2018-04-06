package net.soti.go.plugin.notification.email.model.gocd.plugin;

import net.soti.go.plugin.notification.email.model.MaterialType;
import net.soti.go.plugin.notification.email.model.gocd.plugin.configuration.GitConfig;
import net.soti.go.plugin.notification.email.model.gocd.plugin.configuration.PackageConfig;
import net.soti.go.plugin.notification.email.model.gocd.plugin.configuration.TfsConfig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class PluginMaterial {
    @Expose
    @SerializedName("type")
    private MaterialType type;

    @Expose
    @SerializedName("package-configuration")
    private PackageConfig packageConfiguration;

    @Expose
    @SerializedName("tfs-configuration")
    private TfsConfig tfsConfiguration;

    @Expose
    @SerializedName("git-configuration")
    private GitConfig gitConfiguration;

    @Expose
    @SerializedName("pipeline-configuration")
    private PackageConfig pipelineConfiguration;

    public MaterialType getType() {
        return type;
    }
}

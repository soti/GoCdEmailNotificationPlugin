package net.soti.go.plugin.notification.email.model.gocd.plugin;

import net.soti.go.plugin.notification.email.model.gocd.plugin.configuration.GitConfig;
import net.soti.go.plugin.notification.email.model.gocd.plugin.configuration.PackageConfig;
import net.soti.go.plugin.notification.email.model.gocd.plugin.configuration.TfsConfig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class Material {
    @Expose
    @SerializedName("type")
    public MaterialType type;

    @Expose
    @SerializedName("package-configuration")
    public PackageConfig packageConfiguration;

    @Expose
    @SerializedName("tfs-configuration")
    public TfsConfig tfsConfiguration;

    @Expose
    @SerializedName("git-configuration")
    public GitConfig gitConfiguration;

    @Expose
    @SerializedName("pipeline-configuration")
    public PackageConfig pipelineConfiguration;
}

package net.soti.go.plugin.notification.email.model.gocd.plugin.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class PackageConfig {
    @SerializedName("REPO_ID")
    public String repoId;

    @SerializedName("PACKAGE_PATH")
    public String packagePath;

    @SerializedName("PACKAGE_ID")
    public String packageId;
}

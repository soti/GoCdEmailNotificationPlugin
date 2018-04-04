package net.soti.go.plugin.notification.email.model.gocd.plugin.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class TfsConfig {
    @SerializedName("domain")
    public String domain;

    @SerializedName("url")
    public String url;

    @SerializedName("project-path")
    public String projectPath;
}

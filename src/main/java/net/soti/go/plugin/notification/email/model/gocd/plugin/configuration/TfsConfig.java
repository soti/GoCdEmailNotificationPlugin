package net.soti.go.plugin.notification.email.model.gocd.plugin.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class TfsConfig {
    @SerializedName("domain")
    private String domain;

    @SerializedName("url")
    private String url;

    @SerializedName("project-path")
    private String projectPath;
}

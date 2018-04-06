package net.soti.go.plugin.notification.email.model.gocd.plugin.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class GitConfig {
    @SerializedName("branch")
    private String branch;

    @SerializedName("url")
    private String url;

    @SerializedName("shallow-clone")
    private boolean shallowClone;
}

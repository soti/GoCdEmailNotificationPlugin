package net.soti.go.plugin.notification.email.model.gocd.plugin.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class GitConfig {
    @SerializedName("branch")
    public String branch;

    @SerializedName("url")
    public String url;

    @SerializedName("shallow-clone")
    public boolean shallowClone;
}

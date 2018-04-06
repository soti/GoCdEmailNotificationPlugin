package net.soti.go.plugin.notification.email.model.gocd.plugin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class PluginModification {
    @Expose
    @SerializedName("revision")
    public String revision;

}

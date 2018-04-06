package net.soti.go.plugin.notification.email.model.gocd.plugin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.logging.Logger;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class PluginBuildCause {
    private static final Logger LOG = Logger.getLoggerFor(PluginBuildCause.class);
    @Expose
    @SerializedName("material")
    private PluginMaterial material;
    @Expose
    @SerializedName("changed")
    private boolean changed;
    @Expose
    @SerializedName("modifications")
    private PluginModification[] modifications;

    public PluginMaterial getMaterial() {
        return material;
    }

    public boolean isChanged() {
        return changed;
    }

    public PluginModification[] getModifications() {
        return modifications;
    }
}

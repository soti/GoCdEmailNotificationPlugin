package net.soti.go.plugin.notification.email.model.gocd.plugin;

import net.soti.go.plugin.notification.email.model.PipelineMaterial;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.logging.Logger;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class BuildCause {
    @Expose
    @SerializedName("material")
    public Material material;

    @Expose
    @SerializedName("changed")
    public boolean changed;

    @Expose
    @SerializedName("modifications")
    public Modification[] modifications;

    private static final Logger LOG = Logger.getLoggerFor(BuildCause.class);

    public PipelineMaterial getPipelineMaterial() {
        if (material.type == MaterialType.Pipeline) {
            if (modifications.length < 1) {
                LOG.error("Modification for pipeline should be 1 or more.");
                return null;
            }

            String[] items = modifications[0].revision.split("/");
            if (items.length != 4) {
                LOG.error(String.format("Unexpected revision format for pipeline material, \"%s\".", modifications[0].revision));
                return null;
            }
            return new PipelineMaterial(items[0], items[1]);
        }

        return null;
    }
}

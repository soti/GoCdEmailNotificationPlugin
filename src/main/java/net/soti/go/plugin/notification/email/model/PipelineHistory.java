package net.soti.go.plugin.notification.email.model;

import net.soti.go.plugin.notification.email.model.gocd.Pipeline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-03-18
 */
public class PipelineHistory {
    @Expose
    @SerializedName("pipelines")
    public Pipeline[] pipelines;

    private static final Gson GSON = new GsonBuilder().
            excludeFieldsWithoutExposeAnnotation().
            create();

    @Override
    public String toString() {
        if (pipelines != null && pipelines.length > 0) {
            if (pipelines.length > 1) {
                return pipelines[0].toString() + "...";
            } else {
                return pipelines[0].toString();
            }
        } else {
            return "No history";
        }
    }

    public static PipelineHistory fromJson(String json) {
        return GSON.fromJson(json, PipelineHistory.class);
    }
}


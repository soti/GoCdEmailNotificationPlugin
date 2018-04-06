package net.soti.go.plugin.notification.email.model.gocd.plugin.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class PipelineConfig {
    @SerializedName("pipeline-name")
    private String pipelineName;

    @SerializedName("stage-name")
    private String stageName;
}

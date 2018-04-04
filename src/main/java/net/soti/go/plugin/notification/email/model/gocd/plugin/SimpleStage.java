package net.soti.go.plugin.notification.email.model.gocd.plugin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class SimpleStage {
    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("counter")
    public int counter;

    @Expose
    @SerializedName("approval-type")
    public String approvalType;

    @Expose
    @SerializedName("approved-by")
    public String approvedBy;

    @Expose
    @SerializedName("state")
    public StageStateType state;

    @Expose
    @SerializedName("result")
    public ResultType result;

    @Expose
    @SerializedName("jobs")
    public Job[] jobs;

}

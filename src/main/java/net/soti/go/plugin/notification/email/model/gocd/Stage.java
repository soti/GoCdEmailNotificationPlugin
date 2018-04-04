package net.soti.go.plugin.notification.email.model.gocd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-03-18
 */
public class Stage {
    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("counter")
    public int counter;

    @Expose
    @SerializedName("result")
    public StageResultType result;

    @Expose
    @SerializedName("approved_by")
    public String approvedBy;
}
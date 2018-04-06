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
    private String name;

    @Expose
    @SerializedName("counter")
    private int counter;

    @Expose
    @SerializedName("result")
    private StageResultType result;

    @Expose
    @SerializedName("approved_by")
    private String approvedBy;

    public String getName() {
        return name;
    }

    public int getCounter() {
        return counter;
    }

    public StageResultType getResult() {
        return result;
    }

    public String getApprovedBy() {
        return approvedBy;
    }
}
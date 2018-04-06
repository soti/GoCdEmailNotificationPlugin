package net.soti.go.plugin.notification.email.model.gocd.plugin;

import net.soti.go.plugin.notification.email.model.gocd.StageResultType;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class PluginStage {
    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("counter")
    private int counter;

    @Expose
    @SerializedName("result")
    private StageResultType result;

    /*
    @Expose
    @SerializedName("approval-type")
    public String approvalType;

    @Expose
    @SerializedName("approved-by")
    public String approvedBy;
    */

    public String getName(){
        return name;
    }

    public int getCounter(){
        return counter;
    }

    public StageResultType getResult() {
        return result;
    }
}

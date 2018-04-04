package net.soti.go.plugin.notification.email.model.gocd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-03-18
 */
public class BuildCause {
    @Expose
    @SerializedName("approver")
    public String approver;

    @Expose
    @SerializedName("material_revisions")
    public MaterialRevision[] materialRevisions;
}
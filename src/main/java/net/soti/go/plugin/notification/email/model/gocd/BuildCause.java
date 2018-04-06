package net.soti.go.plugin.notification.email.model.gocd;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-03-18
 */
public class BuildCause {
    @Expose
    @SerializedName("approver")
    private String approver;

    @Expose
    @SerializedName("material_revisions")
    private List<MaterialRevision> materialRevisions;

    public BuildCause() {

    }

    public BuildCause(String approver, List<MaterialRevision> materialRevisions){
        this.approver = approver;
        this.materialRevisions = materialRevisions;
    }

    public List<MaterialRevision> getMaterialRevisions() {
        return materialRevisions;
    }

    public String getApprover() {
        return approver;
    }
}
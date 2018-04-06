package net.soti.go.plugin.notification.email.model.gocd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User: wsim
 * Date: 2018-03-18
 */
public class Modification {
    @Expose
    @SerializedName("revision")
    private String revision;

    @Expose
    @SerializedName("user_name")
    private String userName;

    @Expose
    @SerializedName("comment")
    private String comment;

    @Expose
    @SerializedName("email_address")
    private String email;

    @Expose
    @SerializedName("modified_time")
    private long time;

    public String getRevision() {
        return revision;
    }

    public String getUserName() {
        return userName;
    }

    public String getComment() {
        return comment;
    }

    public String getEmail() {
        return email;
    }

    public long getTime() {
        return time;
    }
}
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
    public String revision;

    @Expose
    @SerializedName("user_name")
    public String userName;

    @Expose
    @SerializedName("comment")
    public String comment;

    @Expose
    @SerializedName("email_address")
    public String email;
}
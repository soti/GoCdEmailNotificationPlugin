package net.soti.go.plugin.notification.email.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.soti.go.plugin.notification.email.utils.LdapManager;

/**
 * User: wsim
 * Date: 2018-03-19
 */
public class ChangedMaterial {
    private static final Pattern GIT_USER_PATTERN = Pattern.compile("^([^<>]+)<([^<>]+)>$");
    private static final Pattern TFS_USER_PATTERN = Pattern.compile("^([^\\\\]+)\\\\([^\\\\]+)$");

    private final String user;
    private final String email;
    private final String link;
    private final MaterialType materialType;
    private final String name;
    private final String revision;
    private final String comment;

    public ChangedMaterial(
            String user,
            String email,
            String link,
            MaterialType materialType,
            String name,
            String revision,
            String comment,
            LdapManager manager) {
        this.link = link;
        this.materialType = materialType;
        this.name = name;
        this.revision = revision;
        this.comment = comment;

        if (email != null && email.length() > 0) {
            this.email = email;
            this.user = user;
        } else {
            Matcher gitMatcher = GIT_USER_PATTERN.matcher(user);
            Matcher tfsMatcher = TFS_USER_PATTERN.matcher(user);
            if (gitMatcher.matches()) {
                this.user = gitMatcher.group(1);
                this.email = gitMatcher.group(2);
            } else if (tfsMatcher.matches()) {
                this.user = tfsMatcher.group(2);
                this.email = manager.findAccountByAccountName(this.user);
            } else {
                this.user = user;
                this.email = "";
            }
        }
    }

    public String getEmail() {
        return email;
    }

    public String getUser() {
        return user;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public MaterialType getType() {
        return materialType;
    }

    public String getRevision(){
        return revision;
    }

    public String getComment() {
        return comment;
    }
}

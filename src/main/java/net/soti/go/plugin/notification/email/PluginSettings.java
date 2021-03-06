/*
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.soti.go.plugin.notification.email;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.soti.go.plugin.notification.email.model.Whitelist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// Implement any settings that your plugin needs
public class PluginSettings {
    private static final Gson GSON = new GsonBuilder().
            excludeFieldsWithoutExposeAnnotation().
            create();

    @Expose
    @SerializedName("go_server_url")
    private String goServerUrl;

    @Expose
    @SerializedName("api_user")
    private String apiUser;

    @Expose
    @SerializedName("api_key")
    private String apiKey;

    @Expose
    @SerializedName("api_url")
    private String apiUrl;

    @Expose
    @SerializedName("ldap_url")
    private String ldapServerUrl;

    @Expose
    @SerializedName("ldap_user")
    private String ldapUser;

    @Expose
    @SerializedName("ldap_key")
    private String ldapKey;

    @Expose
    @SerializedName("smtp_url")
    private String smtpUrl;

    @Expose
    @SerializedName("mail_sender")
    private String sender;

    @Expose
    @SerializedName("whitelist")
    private String whitelist;

    private List<Whitelist> whitelists;

    public String getApiUser() {
        return apiUser;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getGoServerUrl() {
        return goServerUrl;
    }

    public String getLdapServerUrl() {
        return ldapServerUrl;
    }

    public String getLdapUser() {
        return ldapUser;
    }

    public String getLdapKey() {
        return ldapKey;
    }

    public String getMailServerUrl() {
        return smtpUrl;
    }

    public String getSender() {
        return sender;
    }

    public List<Whitelist> getWhitelists() {
        if (whitelists == null) {
            whitelists = Arrays.stream(whitelist.split(","))
                    .map(Whitelist::getWhitelistItem)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return whitelists;
    }

    static PluginSettings fromJSON(String json) {
        return GSON.fromJson(json, PluginSettings.class);
    }
}

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

package net.soti.go.plugin.notification.email.executors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.soti.go.plugin.notification.email.PluginRequest;
import net.soti.go.plugin.notification.email.RequestExecutor;
import net.soti.go.plugin.notification.email.model.ChangedMaterial;
import net.soti.go.plugin.notification.email.model.MaterialType;
import net.soti.go.plugin.notification.email.model.gocd.plugin.StageStateType;
import net.soti.go.plugin.notification.email.requests.StageStatusRequest;
import net.soti.go.plugin.notification.email.utils.GoCdClient;
import net.soti.go.plugin.notification.email.utils.LdapManager;
import net.soti.go.plugin.notification.email.utils.SmtpMailSender;
import net.soti.go.plugin.notification.email.utils.Util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class StageStatusRequestExecutor implements RequestExecutor {
    private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    private final StageStatusRequest request;
    private final PluginRequest pluginRequest;
    private static final Logger LOG = Logger.getLoggerFor(StageStatusRequestExecutor.class);

    public StageStatusRequestExecutor(StageStatusRequest request, PluginRequest pluginRequest) {
        this.request = request;
        this.pluginRequest = pluginRequest;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        HashMap<String, Object> responseJson = new HashMap<>();
        try {
            sendNotification();
            responseJson.put("status", "success");
        } catch (Exception e) {
            responseJson.put("status", "failure");
            responseJson.put("messages", Arrays.asList(e.getMessage()));
        }
        LOG.warn(responseJson.toString());

        return new DefaultGoPluginApiResponse(200, GSON.toJson(responseJson));
    }

    protected void sendNotification() throws Exception {
        StageStateType stageResult = request.pipeline.getChangedState();

        if (stageResult.equals(StageStateType.Failed) || stageResult.equals(StageStateType.Cancelled)) {
            GoCdClient client = new GoCdClient(
                    pluginRequest.getPluginSettings().getApiUrl(),
                    pluginRequest.getPluginSettings().getApiUser(),
                    pluginRequest.getPluginSettings().getApiKey());
            LdapManager manager = new LdapManager(
                    pluginRequest.getPluginSettings().getLdapServerUrl(),
                    pluginRequest.getPluginSettings().getLdapUser(),
                    pluginRequest.getPluginSettings().getLdapKey());
            List<ChangedMaterial> changes = request.pipeline.rootChanges(client, manager);
            List<String> emails = new ArrayList<>();

            changes.stream()
                    .map(ChangedMaterial::getEmail)
                    .filter(email -> email != null && email.length() > 0)
                    .forEach(email -> emails.add(email));

            String bodyTemplate = Util.readResource("/mail_body_template.html");

            StringBuilder sb = new StringBuilder();

            for (ChangedMaterial change : changes) {
                String email = change.getEmail();
                String user = change.getUser();
                String revision = change.getRevision();
                String comment = change.getComment();
                MaterialType type = change.getType();
                String link = change.getLink();
                String repo = change.getName();

                sb.append("<tr>");
                sb.append("<td>");
                sb.append(user);
                if(email != null && email.length() >0){
                    emails.add(email);
                    sb.append(String.format(" <%s>", email));
                }
                sb.append("</td>");

                sb.append("<td>");
                sb.append(type.name());
                sb.append("</td>");

                sb.append("<td>");
                sb.append(String.format("<a href=\"%s\">%s in %s</a>", link, revision, repo));
                sb.append("</td>");

                sb.append("<td>");
                sb.append(comment);
                sb.append("</td>");

                sb.append("</tr>");
            }

            String pipeline = request.pipeline.name;
            String stage = request.pipeline.stage.name;
            String status = stageResult.equals(StageStateType.Failed) ?
                    request.pipeline.isKeepFailing() ?
                            "is keep failing" : "was failed"
                    : "was cancelled";
            String tableBody = sb.toString();
            String gocdLink = String.format("%s/go/pipelines/%s/%d/%s/%d",
                    pluginRequest.getPluginSettings().getGoServerUrl(),
                    pipeline,
                    request.pipeline.counter,
                    stage,
                    request.pipeline.stage.counter);

            String mailBody = String.format(bodyTemplate, pipeline, stage, status, gocdLink,tableBody);
            String subject = String.format("Alert: GoCD Pipeline %s (stage %s) %s.");
            String sender = "Team_DevOps-CA@soti.net";

            SmtpMailSender.sendEmail(subject, mailBody, emails, sender);
        }
    }
}

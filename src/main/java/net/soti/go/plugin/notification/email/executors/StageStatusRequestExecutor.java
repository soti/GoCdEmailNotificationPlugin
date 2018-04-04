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

import java.util.*;

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
    private static final Logger LOG = Logger.getLoggerFor(StageStatusRequestExecutor.class);
    private final StageStatusRequest request;
    private final PluginRequest pluginRequest;

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
            LOG.error("Failed to send notification.", e);
            responseJson.put("status", "failure");
            responseJson.put("messages", Arrays.asList(e.getMessage()));
        }
        LOG.info(responseJson.toString());

        return new DefaultGoPluginApiResponse(200, GSON.toJson(responseJson));
    }

    protected void sendNotification() throws Exception {
        StageStateType stageResult = request.pipeline.getChangedState();
        LOG.info(String.format("Pipeline status change for %s/%d/%s/%d :: %s", request.pipeline.name, request.pipeline.counter, request
                .pipeline.stage.name, request.pipeline.stage.counter, stageResult.name()));

        if (stageResult.equals(StageStateType.Failed) || stageResult.equals(StageStateType.Cancelled)) {
            String pipeline = request.pipeline.name;
            String stage = request.pipeline.stage.name;

            if (pipeline.toLowerCase().contains("_test") || pipeline.contains("Poc")) {
                LOG.info(String.format("Ignore %s of pipeline %s (stage: %s)", request.pipeline.getChangedState().name(), pipeline, stage));
                return;
            }
            if (pipeline.startsWith("Database_") && stage.equals("BackwardCompatibilityTest")) {
                LOG.info(String.format("Ignore %s of pipeline %s (stage: %s)", request.pipeline.getChangedState().name(), pipeline, stage));
                return;
            }
            if (pipeline.startsWith("Acceptance_Aws")) {
                LOG.info(String.format("Ignore %s of pipeline %s (stage: %s)", request.pipeline.getChangedState().name(), pipeline, stage));
                return;
            }
            if (pipeline.startsWith("Acceptance_FeatureToggle_")) {
                LOG.info(String.format("Ignore %s of pipeline %s (stage: %s)", request.pipeline.getChangedState().name(), pipeline, stage));
                return;
            }

            GoCdClient client = new GoCdClient(
                    pluginRequest.getPluginSettings().getApiUrl(),
                    pluginRequest.getPluginSettings().getApiUser(),
                    pluginRequest.getPluginSettings().getApiKey());
            LdapManager manager = new LdapManager(
                    pluginRequest.getPluginSettings().getLdapServerUrl(),
                    pluginRequest.getPluginSettings().getLdapUser(),
                    pluginRequest.getPluginSettings().getLdapKey());
            List<ChangedMaterial> changes = request.pipeline.getChanges(client, manager);

            if (changes.size() == 0) {
                LOG.info("No new changes since last success");
                return;
            }

            HashSet<String> emails = new HashSet();
            changes.stream()
                    .map(ChangedMaterial::getEmail)
                    .filter(email -> email != null && email.length() > 0)
                    .forEach(emails::add);

            String bodyTemplate = Util.readResource("/mail_body_template.html");

            StringBuilder sb = new StringBuilder();
            final Hashtable<String, ChangedMaterial> packageChanges = new Hashtable<>();

            HashSet<String> revisions = new HashSet();
            for (ChangedMaterial change : changes) {
                if (MaterialType.Package.equals(change.getType())) {
                    if (!packageChanges.containsKey(change.getName())) {
                        packageChanges.put(change.getName(), change);
                    }
                    continue;
                }

                if(revisions.contains(change.getRevision()+change.getName())){
                    continue;
                }

                revisions.add(change.getRevision()+change.getName());

                String email = change.getEmail();
                if (email != null && email.length() > 0) {
                    emails.add(email);
                }
                sb.append(getTableBodyString(change));
            }

            for (ChangedMaterial change : packageChanges.values()){
                sb.append(getTableBodyString(change));
            }

            String status = stageResult.equals(StageStateType.Failed) ?
                    request.pipeline.isStillFailing() ?
                            "is still failing" : "was failed"
                    : "was cancelled";
            String tableBody = sb.toString();
            String gocdLink = String.format("%s/go/pipelines/%s/%d/%s/%d",
                    pluginRequest.getPluginSettings().getGoServerUrl(),
                    pipeline,
                    request.pipeline.counter,
                    stage,
                    request.pipeline.stage.counter);

            String mailBody = String.format(bodyTemplate, pipeline, stage, status, gocdLink, tableBody);
            String subject = String.format("Alert: GoCD Pipeline %s (stage %s) %s.", pipeline, stage, status);
            String sender = pluginRequest.getPluginSettings().getSender();

            List<String> emailList = new ArrayList<>();
            emailList.addAll(emails);

            SmtpMailSender mailSender = new SmtpMailSender(pluginRequest.getPluginSettings().getMailServerUrl(), 25, false, sender);
            mailSender.sendEmail(subject, mailBody, emailList, null, null);
        }
    }

    private String getTableBodyString(ChangedMaterial change){
        StringBuilder sb = new StringBuilder();
        MaterialType type = change.getType();
        String repo = change.getName();
        String revision = change.getRevision();
        if (MaterialType.Git.equals(type)) {
            revision = revision.substring(0, 7);
        }

        String email = change.getEmail();
        String user = change.getUser();
        String comment = change.getComment();
        String link = change.getLink();

        sb.append("<tr>");
        sb.append("<td>");
        sb.append(user);
        if (email != null && email.length() > 0) {
            sb.append(String.format(" &lt;%s&gt;", email));
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

        return sb.toString();
    }
}

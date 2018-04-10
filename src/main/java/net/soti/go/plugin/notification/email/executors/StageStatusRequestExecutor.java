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
import net.soti.go.plugin.notification.email.ServerRequestFailedException;
import net.soti.go.plugin.notification.email.model.ChangedMaterial;
import net.soti.go.plugin.notification.email.model.MaterialType;
import net.soti.go.plugin.notification.email.model.Whitelist;
import net.soti.go.plugin.notification.email.model.gocd.ExecutionResultType;
import net.soti.go.plugin.notification.email.model.gocd.StageResultType;
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
        if (StageResultType.Unknown.equals(request.pipeline.getStageResult())) {
            return;
        }

        final String pipelineName = request.pipeline.getName();
        final int pipelineCounter = request.pipeline.getCounter();
        final String stageName = request.pipeline.getStage().getName();
        final int stageCounter = request.pipeline.getStage().getCounter();
        final List<Whitelist> whitelists = pluginRequest.getPluginSettings().getWhitelists();
        whitelists.forEach(item -> LOG.info("Whitelist: " + item.toString()));

        if (whitelists.stream().anyMatch(whitelist -> whitelist.isWhitelisted(pipelineName, stageName))) {
            LOG.info(String.format("Ignores whitelist: [%s] %s/%d/%s/%d",
                    request.pipeline.getStageResult().name(),
                    pipelineName,
                    pipelineCounter,
                    stageName,
                    stageCounter));
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

        int sinceCounter = request.pipeline.initialize(client, manager);

        ExecutionResultType executionResult = request.pipeline.getExecutionResultType();
        if (ExecutionResultType.Passed.equals(executionResult) || ExecutionResultType.Building.equals(executionResult)) {
            return;
        }

        List<ChangedMaterial> changes = request.pipeline.getAllChangedMaterials();
        if (changes.size() == 0) {
            LOG.info("No new changes since last success");
            return;
        }
        LOG.info(String.format("[%s] %s/%d/%s/%d has total %d changes.",
                request.pipeline.getExecutionResultType().name(),
                pipelineName,
                pipelineCounter,
                stageName,
                stageCounter,
                changes.size()));

        HashSet<String> emails = new HashSet<>();
        changes.stream().map(ChangedMaterial::getEmail)
                .filter(email -> email != null && email.length() > 0)
                .forEach(emails::add);

        String bodyTemplate = Util.readResource("/mail_body_template.html");

        StringBuilder sb = new StringBuilder();
        final HashSet<String> usedRevisions = new HashSet<>();
        final List<String> tableBodies = new ArrayList<>();

        for (int i = changes.size() - 1; i >= 0; i--) {
            ChangedMaterial change = changes.get(i);
            String key = change.getType().name() + change.getRevision() + change.getName() + change.getPipelineName();
            if (usedRevisions.contains(key)) {
                continue;
            }

            usedRevisions.add(key);
            String email = change.getEmail();
            if (email != null && email.length() > 0) {
                emails.add(email);
            }
            tableBodies.add(getTableBodyString(change));
        }

        for (String body : tableBodies) {
            sb.append(body);
        }

        String tableBody = sb.toString();
        String statusMessage;
        switch (executionResult) {
            case Broken:
                statusMessage = "was broken";
                break;
            case Failing:
                statusMessage = "is still failing";
                break;
            case Fixed:
                statusMessage = "has been fixed";
                break;
            default:
                statusMessage = "passed";
        }

        String gocdLink = String.format("%s/go/pipelines/%s/%d/%s/%d",
                pluginRequest.getPluginSettings().getGoServerUrl(),
                pipelineName,
                pipelineCounter,
                stageName,
                stageCounter);

        String subject = String.format("[%s] pipeline '%s' (stage '%s') %s",
                executionResult.name(), pipelineName, stageName, statusMessage);
        String sinceLink = String.format("%s/go/pipelines/%s/%d/%s/%d",
                pluginRequest.getPluginSettings().getGoServerUrl(),
                pipelineName,
                sinceCounter,
                stageName,
                stageCounter);
        String sinceMessage = ExecutionResultType.Fixed.equals(executionResult) ?
                "while it was failing" :
                sinceCounter > 0 ?
                        String.format("since last successful build <a href=\"%s\">(%s/%d)</a>", sinceLink, pipelineName, sinceCounter) :
                        String.format("from the beginning of the pipeline history (never passed)");

        String mailBody = String.format(bodyTemplate,
                pipelineName, pipelineCounter, stageName, stageCounter, statusMessage, gocdLink, sinceMessage, tableBody);
        String sender = pluginRequest.getPluginSettings().getSender();

        List<String> emailList = new ArrayList<>();
        emailList.addAll(emails);
        LOG.debug(String.format("Sends mail to %d recepients.", emails.size()));
        SmtpMailSender mailSender = new SmtpMailSender(pluginRequest.getPluginSettings().getMailServerUrl(), 25, false, sender);
        mailSender.sendEmail(subject, mailBody, emailList, null, null);
    }

    private String getTableBodyString(ChangedMaterial change) throws ServerRequestFailedException {
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
            sb.append(String.format("<br/>&lt;%s&gt;", email));
        }
        sb.append("</td>");

        sb.append("<td>");
        String linkUrl = String.format("%s/go/pipelines/%s/%d/%s/%d",
                pluginRequest.getPluginSettings().getGoServerUrl(),
                change.getPipelineName(),
                change.getPipelineCounter(),
                change.getStageName(),
                change.getStageCounter());

        sb.append(String.format("<a href=\"%s\">%s [%d]</a>", linkUrl, change.getPipelineName(), change.getPipelineCounter()));
        sb.append("</td>");

        sb.append("<td>");
        sb.append(String.format("%s: <a href=\"%s\">%s</a>", type.name(), link, revision));
        sb.append("</td>");

        sb.append("<td style=\"text-align: left;\">");
        sb.append(String.format("[At %s]<br/><br/>%s", MaterialType.Package.equals(type) ? link : repo, comment));
        sb.append("</td>");

        sb.append("</tr>");

        return sb.toString();
    }
}

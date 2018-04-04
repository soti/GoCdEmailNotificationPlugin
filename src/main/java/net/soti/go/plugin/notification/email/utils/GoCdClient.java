package net.soti.go.plugin.notification.email.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import net.soti.go.plugin.notification.email.model.PipelineHistory;
import net.soti.go.plugin.notification.email.model.gocd.Pipeline;
import net.soti.go.plugin.notification.email.model.gocd.Stage;
import net.soti.go.plugin.notification.email.model.gocd.StageResultType;

import com.thoughtworks.go.plugin.api.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * User: wsim
 * Date: 2018-04-03
 */
public class GoCdClient {
    private final String getPipelineUrlFormat;
    private final String user;
    private final String password;

    private static final Logger LOG = Logger.getLoggerFor(GoCdClient.class);

    public GoCdClient(String goCdApiUrl, String user, String password) {
        this.getPipelineUrlFormat = String.format("%s/go/api/pipelines/%s/history/%s", goCdApiUrl, "%s", "%d");
        this.user = user;
        this.password = password;
    }

    public List<Pipeline> getPipelineHistorySinceLastSuccess(String pipelineName, String pipelineCounter, String stageName) throws
            IOException {
        long offset = 0;
        boolean completed = false;
        ArrayList<Pipeline> history = new ArrayList<>();
        LOG.warn(String.format("getPipelineHistorySinceLastSuccess %s %s %s", pipelineName, pipelineCounter, stageName));
        do {
            Pipeline[] pipelineHistory = getPipelineHistory(pipelineName, offset);
            final long numberOfPipelines = pipelineHistory.length;
            List<Pipeline> pipelines = Arrays.stream(pipelineHistory).filter(pipeline -> pipeline.counter <= Integer.parseInt
                    (pipelineCounter)).collect(Collectors.toList());

            Optional<Pipeline> lastPassed = Optional.empty();
            for (Pipeline pipeline : pipelines) {
                boolean found = false;
                for (Stage stage : pipeline.stages) {
                    if (stage.name.equalsIgnoreCase(stageName) && stage.result.equals(StageResultType.Passed)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    lastPassed = Optional.of(pipeline);
                }
            }
            boolean isLastHistory = pipelines.stream().anyMatch(pipeline -> pipeline.counter == 1);
            if (lastPassed.isPresent()) {
                final Pipeline last = lastPassed.get();
                final Pipeline first = Arrays.stream(pipelineHistory).findFirst().get();
                pipelines = pipelines.stream()
                        .filter(pipeline
                                -> {
                            if (first.counter == last.counter && history.size() == 0) {
                                return pipeline.counter == first.counter;
                            }
                            return pipeline.counter > last.counter;
                        }).collect(Collectors.toList());
                completed = true;
            } else if (isLastHistory) {
                completed = true;
            } else {
                offset += numberOfPipelines;
                LOG.warn(String.format("getPipelineHistorySinceLastSuccess, go more %d", offset));
            }

            history.addAll(pipelines);
        } while (!completed);

        LOG.warn(String.format("getPipelineHistorySinceLastSuccess, return size %d", history.size()));
        return history;
    }

    private Pipeline[] getPipelineHistory(String pipelineName, long offset) throws IOException {
        String url = String.format(getPipelineUrlFormat, pipelineName, offset);
        HttpResult result = httpGet(url);
        if (!result.isSuccessResult()) {
            throw new IOException(String.format("Failed to read history of \"\".", pipelineName));
        }
        PipelineHistory history = PipelineHistory.fromJson(result.getData());
        return history.pipelines;
    }

    private HttpResult httpGet(String url) throws IOException {
        LOG.warn(String.format("httpGet %s", url));

        CloseableHttpClient httpClient = null;

        try {
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials
                    = new UsernamePasswordCredentials(user, password);
            provider.setCredentials(AuthScope.ANY, credentials);

            httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            LOG.warn(String.format("httpGet result %s", response.toString()));
            return HttpResult.fromResponse(response);
        } finally {
            closeHttpSilently(httpClient);
        }
    }

    private void closeHttpSilently(CloseableHttpClient httpClient) {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

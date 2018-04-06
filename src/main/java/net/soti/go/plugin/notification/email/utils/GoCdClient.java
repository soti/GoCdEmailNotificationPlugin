package net.soti.go.plugin.notification.email.utils;

import java.io.IOException;
import java.security.InvalidParameterException;
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
    private static final Logger LOG = Logger.getLoggerFor(GoCdClient.class);
    private final String getPipelinesUrlFormat;
    private final String getPipelineUrlFormat;
    private final String user;
    private final String password;

    public GoCdClient(String goCdApiUrl, String user, String password) {
        this.getPipelinesUrlFormat = String.format("%s/go/api/pipelines/%s/history/%s", goCdApiUrl, "%s", "%d");
        this.getPipelineUrlFormat = String.format("%s/go/api/pipelines/%s/instance/%s", goCdApiUrl, "%s", "%d");
        this.user = user;
        this.password = password;
    }

    public Pipeline getPipeline(String pipelineName, int pipelineCounter) throws IOException {
        String url = String.format(getPipelineUrlFormat, pipelineName, pipelineCounter);
        HttpResult result = httpGet(url);
        if (!result.isSuccessResult()) {
            throw new IOException(String.format("Failed to read history of \"\".", pipelineName));
        }
        return Pipeline.fromJson(result.getData());
    }

    public List<Pipeline> getPipelinesBetween(String pipelineName, int startCounter, int endCounter) throws IOException {
        if (startCounter > endCounter) {
            throw new InvalidParameterException(
                    String.format("Start counter (%d) of pipeline '%s' should be equal to or smaller than end counter (%d).",
                            startCounter,
                            pipelineName,
                            endCounter));
        }

        if (startCounter < 1) {
            throw new InvalidParameterException(
                    String.format("Start counter (%d) of pipeline '%s' should be bigger than 0.",
                            startCounter,
                            pipelineName));
        }

        long offset = 0;
        ArrayList<Pipeline> history = new ArrayList<>();
        do {
            Pipeline[] pipelineHistory = getPipelinesBetween(pipelineName, offset);
            Arrays.stream(pipelineHistory)
                    .filter(pipeline -> pipeline.getCounter() <= endCounter && pipeline.getCounter() >= startCounter)
                    .forEach(pipeline -> history.add(pipeline));

            if (history.size() == 0) {
                continue;
            }
            if (history.get(history.size() - 1).getCounter() == startCounter ||
                    pipelineHistory.length == 0 ||
                    pipelineHistory[pipelineHistory.length - 1].getCounter() == 1) {
                break;
            }

            offset += pipelineHistory.length;
        } while (true);

        return history;
    }

    public List<Pipeline> getPipelineHistorySinceLastSuccess(String pipelineName, int pipelineCounter, final String stageName) throws
            IOException {
        long offset = 0;
        boolean completed = false;
        ArrayList<Pipeline> history = new ArrayList<>();
        do {
            Pipeline[] pipelineHistory = getPipelinesBetween(pipelineName, offset);
            final long numberOfPipelines = pipelineHistory.length;
            List<Pipeline> pipelines = Arrays.stream(pipelineHistory)
                    .filter(pipeline -> pipeline.getCounter() <= pipelineCounter)
                    .collect(Collectors.toList());

            Optional<Pipeline> lastPassed = Optional.empty();
            for (Pipeline pipeline : pipelines) {
                for (Stage stage : pipeline.getStages()) {
                    if (stageName.equalsIgnoreCase(stage.getName()) && StageResultType.Passed.equals(stage.getResult())) {
                        lastPassed = Optional.of(pipeline);
                        break;
                    }
                }
                if (lastPassed.isPresent()) {
                    break;
                }
            }

            boolean isLastHistory = pipelines.stream().anyMatch(pipeline -> pipeline.getCounter() == 1);
            if (lastPassed.isPresent()) {
                final Pipeline last = lastPassed.get();
                pipelines = pipelines.stream()
                        .filter(pipeline -> pipeline.getCounter() >= last.getCounter())
                        .collect(Collectors.toList());
                completed = true;
            } else if (isLastHistory) {
                completed = true;
            } else {
                offset += numberOfPipelines;
            }

            history.addAll(pipelines);
        } while (!completed);

        return history;
    }

    private Pipeline[] getPipelinesBetween(String pipelineName, long offset) throws IOException {
        String url = String.format(getPipelinesUrlFormat, pipelineName, offset);
        HttpResult result = httpGet(url);
        if (!result.isSuccessResult()) {
            throw new IOException(String.format("Failed to read history of \"\".", pipelineName));
        }
        PipelineHistory history = PipelineHistory.fromJson(result.getData());
        return history.pipelines;
    }

    private HttpResult httpGet(String url) throws IOException {
        CloseableHttpClient httpClient = null;

        try {
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials
                    = new UsernamePasswordCredentials(user, password);
            provider.setCredentials(AuthScope.ANY, credentials);
            httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);

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

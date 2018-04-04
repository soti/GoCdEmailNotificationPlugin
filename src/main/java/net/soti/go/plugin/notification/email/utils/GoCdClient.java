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
    private final String getPipelinesUrlFormat;
    private final String getPipelineUrlFormat;
    private final String user;
    private final String password;

    private static final Logger LOG = Logger.getLoggerFor(GoCdClient.class);

    public GoCdClient(String goCdApiUrl, String user, String password) {
        this.getPipelinesUrlFormat = String.format("%s/go/api/pipelines/%s/history/%s", goCdApiUrl, "%s", "%d");
        this.getPipelineUrlFormat = String.format("%s/go/api/pipelines/%s/instance/%s", goCdApiUrl, "%s", "%d");
        this.user = user;
        this.password = password;
    }

    public Pipeline getPipeline(String pipelineName, int pipelineCounter) throws IOException{
        String url = String.format(getPipelineUrlFormat, pipelineName, pipelineCounter);
        HttpResult result = httpGet(url);
        if (!result.isSuccessResult()) {
            throw new IOException(String.format("Failed to read history of \"\".", pipelineName));
        }
        return Pipeline.fromJson(result.getData());
    }

    public List<Pipeline> getPipelineHistorySinceLastSuccess(String pipelineName, String pipelineCounter, String stageName) throws
            IOException {
        long offset = 0;
        boolean completed = false;
        ArrayList<Pipeline> history = new ArrayList<>();
        do {
            Pipeline[] pipelineHistory = getPipelineHistory(pipelineName, offset);
            final long numberOfPipelines = pipelineHistory.length;
            List<Pipeline> pipelines = Arrays.stream(pipelineHistory).filter(pipeline -> pipeline.counter <= Integer.parseInt
                    (pipelineCounter)).collect(Collectors.toList());

            Optional<Pipeline> lastPassed = Optional.empty();
            for (Pipeline pipeline : pipelines) {
                for (Stage stage : pipeline.stages) {
                    if (stageName.equalsIgnoreCase(stage.name) && StageResultType.Passed.equals(stage.result)) {
                        lastPassed = Optional.of(pipeline);
                        break;
                    }
                }

                if (lastPassed.isPresent()) {
                    break;
                }
            }

            boolean isLastHistory = pipelines.stream().anyMatch(pipeline -> pipeline.counter == 1);
            if (lastPassed.isPresent()) {
                final Pipeline last = lastPassed.get();
                final Pipeline first = Arrays.stream(pipelineHistory).findFirst().get();
                List<Pipeline> newPipelines = new ArrayList<>();
                for (Pipeline pipeline : pipelines){
                    if (history.size() == 0 && first.counter == last.counter ){
                        if(pipeline.counter < first.counter){
                            break;
                        }
                    }
                    if(pipeline.counter <= last.counter){
                        break;
                    }
                    newPipelines.add(pipeline);
                }
                pipelines = newPipelines;
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

    private Pipeline[] getPipelineHistory(String pipelineName, long offset) throws IOException {
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

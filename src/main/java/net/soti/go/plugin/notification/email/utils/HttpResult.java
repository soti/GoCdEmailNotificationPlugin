package net.soti.go.plugin.notification.email.utils;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

/**
 * User: wsim
 * Date: 2018-04-03
 */
class HttpResult {
    private final int statusCode;
    private final String data;

    private HttpResult(int statusCode, String data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    boolean isSuccessResult() {
        return statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NO_CONTENT || statusCode == HttpStatus.SC_CREATED;
    }

    String getData() {
        return data;
    }

    static HttpResult fromResponse(HttpResponse response) throws IOException {
        return new HttpResult(response.getStatusLine().getStatusCode(), entityToStringOrNull(response.getEntity()));
    }

    private static String entityToStringOrNull(HttpEntity entity) throws IOException {
        return (entity != null) ? EntityUtils.toString(entity, "UTF-8") : null;
    }
}

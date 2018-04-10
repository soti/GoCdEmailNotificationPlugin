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

import java.util.HashMap;

import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GetPluginConfigurationExecutorTest {

    @Test
    public void shouldSerializeAllFields() throws Exception {
        GoPluginApiResponse response = new GetPluginConfigurationExecutor().execute();
        HashMap hashMap = new Gson().fromJson(response.responseBody(), HashMap.class);
        assertEquals("Are you using anonymous inner classes — see https://github.com/google/gson/issues/298",
                hashMap.size(),
                GetPluginConfigurationExecutor.FIELDS.size()
        );
    }

    @Test
    public void assertJsonStructure() throws Exception {
        GoPluginApiResponse response = new GetPluginConfigurationExecutor().execute();

        assertThat(response.responseCode(), CoreMatchers.is(200));
        String expectedJSON = "{\n" +
                "  \"go_server_url\": {\n" +
                "    \"display-name\": \"Go Server URL\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"0\"\n" +
                "  },\n" +
                "  \"api_url\": {\n" +
                "    \"display-name\": \"API URL\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"1\"\n" +
                "  },\n" +
                "  \"api_user\": {\n" +
                "    \"display-name\": \"API User\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"2\"\n" +
                "  },\n" +
                "  \"api_key\": {\n" +
                "    \"display-name\": \"API Key\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"3\"\n" +
                "  },\n" +
                "  \"ldap_url\": {\n" +
                "    \"display-name\": \"LDAP URL\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"4\"\n" +
                "  },\n" +
                "  \"ldap_user\": {\n" +
                "    \"display-name\": \"LDAP User\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"5\"\n" +
                "  },\n" +
                "  \"ldap_key\": {\n" +
                "    \"display-name\": \"LDAP Key\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"6\"\n" +
                "  },\n" +
                "  \"smtp_url\": {\n" +
                "    \"display-name\": \"SMTP URL\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"7\"\n" +
                "  },\n" +
                "  \"mail_sender\": {\n" +
                "    \"display-name\": \"Mail Sender\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"8\"\n" +
                "  },\n" +
                "  \"whitelist\": {\n" +
                "    \"display-name\": \"Notification excluded pipelines/stages\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"9\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);

    }
}

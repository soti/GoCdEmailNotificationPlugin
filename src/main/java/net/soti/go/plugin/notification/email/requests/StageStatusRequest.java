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

package net.soti.go.plugin.notification.email.requests;

import net.soti.go.plugin.notification.email.PluginRequest;
import net.soti.go.plugin.notification.email.RequestExecutor;
import net.soti.go.plugin.notification.email.executors.StageStatusRequestExecutor;
import net.soti.go.plugin.notification.email.model.gocd.plugin.PluginPipeline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class StageStatusRequest {
    @Expose
    public PluginPipeline pipeline;

    static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static StageStatusRequest fromJSON(String json) {
        return GSON.fromJson(json, StageStatusRequest.class);
    }

    public RequestExecutor executor(PluginRequest pluginRequest) {
        return new StageStatusRequestExecutor(this, pluginRequest);
    }
}

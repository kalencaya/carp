/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.sliew.carp.module.application.vela.api.v1.model.v1;

import cn.sliew.carp.module.application.vela.api.v1.model.V1History;
import cn.sliew.carp.module.application.vela.api.v1.model.V1RootFS;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class V1ConfigFile {
    @JsonProperty("architecture")
    private String architecture = null;

    @JsonProperty("author")
    private String author = null;

    @JsonProperty("config")
    private V1Config config = null;

    @JsonProperty("container")
    private String container = null;

    @JsonProperty("created")
    private String created = null;

    @JsonProperty("docker_version")
    private String dockerVersion = null;

    @JsonProperty("history")
    private List<V1History> history = null;

    @JsonProperty("os")
    private String os = null;

    @JsonProperty("os.features")
    private List<String> osFeatures = null;

    @JsonProperty("os.version")
    private String osVersion = null;

    @JsonProperty("rootfs")
    private V1RootFS rootfs = null;

    @JsonProperty("variant")
    private String variant = null;

}


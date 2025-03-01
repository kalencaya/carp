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
package cn.sliew.carp.module.orca.spinnaker.api.model.task;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

public interface TaskContext {

    ImmutableMap<String, Object> getInputs();

    default ImmutableMap<String, Object> getInputs(String prefix) {
        final String prefixWithDot = format("%s.", prefix);

        return ImmutableMap.copyOf(
                getInputs().entrySet().stream()
                        .filter(it -> it.getKey().startsWith(prefixWithDot))
                        .collect(
                                toMap(it -> it.getKey().substring(prefixWithDot.length()), Map.Entry::getValue)));
    }
}

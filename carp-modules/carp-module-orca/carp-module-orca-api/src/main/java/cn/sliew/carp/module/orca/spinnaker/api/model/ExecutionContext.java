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
package cn.sliew.carp.module.orca.spinnaker.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ExecutionContext {

    private static final ThreadLocal<ExecutionContext> threadLocal = new ThreadLocal<>();

    private final String executionType;
    private final Long executionId;
    private final Long stageId;
    private final String origin;
    private final Instant stageStartTime;

    public static void set(ExecutionContext executionContext) {
        threadLocal.set(executionContext);
    }

    public static ExecutionContext get() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}

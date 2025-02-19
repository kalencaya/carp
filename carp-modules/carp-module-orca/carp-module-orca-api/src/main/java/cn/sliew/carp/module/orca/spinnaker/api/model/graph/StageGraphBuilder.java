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
package cn.sliew.carp.module.orca.spinnaker.api.model.graph;

import cn.sliew.carp.framework.dag.service.dto.DagStepDTO;

import java.util.function.Consumer;

/**
 * Provides a low-level API for manipulating a stage DAG.
 */
public interface StageGraphBuilder {

    void add(DagStepDTO stage);

    DagStepDTO add(Consumer<DagStepDTO> init);

    default void connect(DagStepDTO previous, DagStepDTO next) {
        throw new UnsupportedOperationException();
    }

    default DagStepDTO connect(DagStepDTO previous, Consumer<DagStepDTO> init) {
        DagStepDTO stage = add(init);
        connect(previous, stage);
        return stage;
    }

    void append(DagStepDTO stage);

    DagStepDTO append(Consumer<DagStepDTO> init);

    Iterable<DagStepDTO> build();
}

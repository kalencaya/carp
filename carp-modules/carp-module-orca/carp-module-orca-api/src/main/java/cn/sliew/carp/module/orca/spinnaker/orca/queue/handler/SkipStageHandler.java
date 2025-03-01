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
package cn.sliew.carp.module.orca.spinnaker.orca.queue.handler;

import cn.sliew.carp.module.orca.spinnaker.api.model.ExecutionStatus;
import cn.sliew.carp.module.orca.spinnaker.api.model.stage.StageExecutionImpl;
import cn.sliew.carp.module.orca.spinnaker.api.model.util.StageExecutionUtil;
import cn.sliew.carp.module.orca.spinnaker.api.persistence.ExecutionRepository;
import cn.sliew.carp.module.orca.spinnaker.keiko.core.Queue;
import cn.sliew.carp.module.orca.spinnaker.orca.core.events.StageComplete;
import cn.sliew.carp.module.orca.spinnaker.orca.queue.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.EnumSet;

@Slf4j
@Component
public class SkipStageHandler extends AbstractOrcaMessageHandler<Messages.SkipStage> {

    private static final EnumSet<ExecutionStatus> RUNNING_OR_NOT_STARTED = EnumSet.of(ExecutionStatus.RUNNING, ExecutionStatus.NOT_STARTED);
    private static final EnumSet<ExecutionStatus> TERMINAL_STATUSES = EnumSet.of(ExecutionStatus.SUCCEEDED, ExecutionStatus.TERMINAL, ExecutionStatus.FAILED_CONTINUE);

    public SkipStageHandler(
            Queue queue,
            ExecutionRepository repository,
            ApplicationEventPublisher publisher,
            Clock clock) {
        super(queue, repository, publisher, clock);
    }

    @Override
    public Class<Messages.SkipStage> getMessageType() {
        return Messages.SkipStage.class;
    }

    @Override
    public void handle(Messages.SkipStage message) {
        withStage(message, stage -> {
            if (RUNNING_OR_NOT_STARTED.contains(stage.getStatus()) || StageExecutionUtil.isManuallySkipped(stage)) {
                ((StageExecutionImpl) stage).setStatus(ExecutionStatus.SKIPPED);
                if (StageExecutionUtil.isManuallySkipped(stage)) {
                    StageExecutionUtil.recursiveSyntheticStages(stage)
                            .forEach(it -> {
                                if (!TERMINAL_STATUSES.contains(it.getStatus())) {
                                    ((StageExecutionImpl) it).setStatus(ExecutionStatus.SKIPPED);
                                    ((StageExecutionImpl) it).setEndTime(clock.instant());
                                    getRepository().storeStage(it);
                                    publisher.publishEvent(new StageComplete(this, it));
                                }
                            });
                }
                ((StageExecutionImpl) stage).setEndTime(clock.instant());
                getRepository().storeStage(stage);
                startNext(stage);
                publisher.publishEvent(new StageComplete(this, stage));
            }
        });
    }

}

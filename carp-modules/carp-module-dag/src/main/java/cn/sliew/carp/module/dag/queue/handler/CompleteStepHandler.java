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
package cn.sliew.carp.module.dag.queue.handler;

import cn.sliew.carp.framework.dag.algorithm.DAG;
import cn.sliew.carp.framework.dag.algorithm.DagUtil;
import cn.sliew.carp.framework.dag.service.DagInstanceComplexService;
import cn.sliew.carp.framework.dag.service.dto.DagInstanceComplexDTO;
import cn.sliew.carp.framework.dag.service.dto.DagStepDTO;
import cn.sliew.carp.module.dag.queue.Messages;
import cn.sliew.carp.module.dag.util.DagExecutionUtil;
import cn.sliew.carp.module.workflow.stage.model.ExecutionStatus;
import cn.sliew.carp.module.workflow.stage.model.task.TaskExecutionImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CompleteStepHandler extends AbstractDagMessageHandler<Messages.CompleteStep>{

    @Autowired
    private DagInstanceComplexService dagInstanceComplexService;

    @Override
    public Class<Messages.CompleteStep> getMessageType() {
        return Messages.CompleteStep.class;
    }

    @Override
    public void handle(Messages.CompleteStep message) {
        withStep(message, dagStepDTO -> {
            Set<String> statuses = Set.of(ExecutionStatus.RUNNING.name(), ExecutionStatus.NOT_STARTED.name());
            if (statuses.contains(dagStepDTO.getStatus())) {
                ExecutionStatus status = determineStatus(dagStepDTO);
//                if (shouldFailOnFailedExpressionEvaluation(stage)) {
//                    log.warn(
//                            "Stage {} ({}) of {} is set to fail because of failed expressions.",
//                            stage.getId(),
//                            stage.getType(),
//                            stage.getPipelineExecution().getId()
//                    );
//                    status = ExecutionStatus.TERMINAL;
//                }

                try {
                    if (statuses.contains(dagStepDTO.getStatus()) || (status.isComplete() && !status.isHalt())) {
                        // 检查此阶段是否有任何未计划的合成后置阶段
//                        List<StageExecution> afterStages = StageExecutionUtil.firstAfterStages(stage);
//                        if (CollectionUtils.isEmpty(afterStages)) {
//                            planAfterStages(stage);
//                            afterStages = StageExecutionUtil.firstAfterStages(stage);
//                        }
//                        if (!afterStages.isEmpty() && afterStages.stream().anyMatch(it -> it.getStatus() == ExecutionStatus.NOT_STARTED)) {
//                            afterStages.stream()
//                                    .filter(it -> it.getStatus() == ExecutionStatus.NOT_STARTED)
//                                    .forEach(it -> getQueue().push(new Messages.StartStage(message, it.getId())));
//                            return;
//                        } else if (status == ExecutionStatus.NOT_STARTED) {
//                            // 阶段没有合成阶段或任务，这很奇怪但无论如何
//                            log.warn("Stage {} ({}) of {} had no tasks or synthetic stages!",
//                                    stage.getId(), stage.getType(), stage.getPipelineExecution().getId());
//                            status = ExecutionStatus.SKIPPED;
//                        }

                        if (status == ExecutionStatus.NOT_STARTED) {
                            // 阶段没有合成阶段或任务，这很奇怪但无论如何
                            log.warn("Stage {} ({}) of {} had no tasks or synthetic stages!",
                                    dagStepDTO.getId(), dagStepDTO.getDagConfigStep().getStepName(), dagStepDTO.getDagInstance().getId());
                            status = ExecutionStatus.SKIPPED;
                        }
                    } else if (status.isFailure()) {
//                        boolean hasOnFailureStages = planOnFailureStages(stage);
//
//                        if (hasOnFailureStages) {
//                            StageExecutionUtil.firstAfterStages(stage).forEach(it -> getQueue().push(new Messages.StartStage(it)));
//                            return;
//                        }
                    }

                    dagStepDTO.setStatus(status.name());
                    if (status == ExecutionStatus.FAILED_CONTINUE) {
                        handleFailedContinue(dagStepDTO);
                    }
                    dagStepDTO.setEndTime(new Date());
                } catch (Exception e) {
                    log.error("Failed to construct after stages for {} {}", dagStepDTO.getDagConfigStep().getStepName(), dagStepDTO.getId(), e);

//                    ExceptionVO exceptionVO = handleException(dagStepDTO.getDagConfigStep().getStepName() + ":ConstructAfterStages", e);
//                    stage.getContext().put("exception", exceptionDetails);
                    dagStepDTO.setStatus(ExecutionStatus.TERMINAL.name());
                    dagStepDTO.setEndTime(new Date());
                }

//                includeExpressionEvaluationSummary(stage);
//                getRepository().storeStage(stage);

                EnumSet<ExecutionStatus> set = EnumSet.of(ExecutionStatus.SUCCEEDED, ExecutionStatus.FAILED_CONTINUE, ExecutionStatus.SKIPPED);
                // 当合成阶段以 FAILED_CONTINUE 结束时，将该状态传播到阶段的父级
                // 这样父级的其他合成子阶段就不会运行
                if (StringUtils.equalsIgnoreCase(dagStepDTO.getStatus(), ExecutionStatus.FAILED_CONTINUE)
//                        && stage.getSyntheticStageOwner() != null
//                        && !stage.getAllowSiblingStagesToContinueOnFailure()
                ) {
//                    Messages.CompleteStep copyMessage = new Messages.CompleteStep(message, dagStepDTO.getParentStageId());
//                    push(copyMessage);
                } else if (set.contains(dagStepDTO.getStatus())) {
                    startNext(dagStepDTO);
                } else {
                    push(new Messages.CancelStep(message));
                    push(new Messages.CompleteDag(message));
//                    if (dagStepDTO.getSyntheticStageOwner() == null) {
//                        log.debug("Stage has no synthetic owner and status is {}, completing execution (original message: {})",
//                                dagStepDTO.getStatus(), message);
//                        push(new Messages.CompleteDag(message));
//                    } else {
//                        Messages.CompleteStep copyMessage = new Messages.CompleteStep(message, dagStepDTO.getParentStageId());
//                        push(copyMessage);
//                    }
                }

//                publisher.publishEvent(new StageComplete(this, stage));
            }
        });
    }


    private ExecutionStatus determineStatus(DagStepDTO step) {
//        List<ExecutionStatus> syntheticStatuses = DagExecutionUtil.syntheticStages(step).stream()
//                .map(DagStepDTO::getStatus)
//                .collect(Collectors.toList());

        List<ExecutionStatus> taskStatuses = DagExecutionUtil.getTasks(step).stream()
                .map(TaskExecutionImpl::getStatus)
                .collect(Collectors.toList());

//        List<ExecutionStatus> planningStatus = hasPlanningFailure(step) ?
//                Collections.singletonList(DagExecutionUtil.failureStatus(step, null)) :
//                Collections.emptyList();

        List<ExecutionStatus> allStatuses = new ArrayList<>();
//        allStatuses.addAll(syntheticStatuses);
        allStatuses.addAll(taskStatuses);
//        allStatuses.addAll(planningStatus);

//        List<ExecutionStatus> afterStageStatuses = StageExecutionUtil.afterStages(stage).stream()
//                .map(StageExecution::getStatus)
//                .collect(Collectors.toList());

        if (allStatuses.isEmpty()) {
            return ExecutionStatus.NOT_STARTED;
        } else if (allStatuses.contains(ExecutionStatus.TERMINAL)) {
            return DagExecutionUtil.failureStatus(step, null); // 正确处理配置的"如果阶段失败"选项
        } else if (allStatuses.contains(ExecutionStatus.STOPPED)) {
            return ExecutionStatus.STOPPED;
        } else if (allStatuses.contains(ExecutionStatus.CANCELED)) {
            return ExecutionStatus.CANCELED;
        } else if (allStatuses.contains(ExecutionStatus.FAILED_CONTINUE)) {
            return ExecutionStatus.FAILED_CONTINUE;
        } else if (allStatuses.stream().allMatch(it -> it == ExecutionStatus.SUCCEEDED || it == ExecutionStatus.SKIPPED)) {
            return ExecutionStatus.SUCCEEDED;
//        } else if (afterStageStatuses.contains(ExecutionStatus.NOT_STARTED)) {
//            return ExecutionStatus.RUNNING; // 后置阶段已计划但尚未运行
        } else {
            log.error("Unhandled condition for stage (id={}) of pipeline (id={}), marking as TERMINAL. " +
                            "syntheticStatuses={}, taskStatuses={}, planningStatus={}, afterStageStatuses={}",
                    step.getId(),
                    step.getDagInstance().getId(),
//                    syntheticStatuses,
                    null,
                    taskStatuses,
//                    planningStatus,
                    null,
//                    afterStageStatuses
                    null
            );

            return ExecutionStatus.TERMINAL;
        }
    }


    private void handleFailedContinue(DagStepDTO step) {
//        @SuppressWarnings("unchecked")
//        Map<String, Object> exception = (Map<String, Object>) step.getContext().get("exception");
//        if (exception != null) {
//            @SuppressWarnings("unchecked")
//            Map<String, Object> details = (Map<String, Object>) exception.get("details");
//            if (details != null) {
//                @SuppressWarnings("unchecked")
//                List<String> errors = (List<String>) details.get("errors");
//                List<String> updatedErrors = Lists.newArrayList();
//
//                step.getTasks().stream()
//                        .filter(t -> !t.getTaskExceptionDetails().isEmpty())
//                        .forEach(task -> {
//                            @SuppressWarnings("unchecked")
//                            Map<String, Object> taskException =
//                                    (Map<String, Object>) task.getTaskExceptionDetails().get("exception");
//                            @SuppressWarnings("unchecked")
//                            Map<String, Object> taskDetails =
//                                    (Map<String, Object>) taskException.get("details");
//
//                            updatedErrors.add(task.getName() + ":");
//                            updatedErrors.add((String) taskDetails.get("error"));
//                            @SuppressWarnings("unchecked")
//                            List<String> taskErrors = (List<String>) taskDetails.get("errors");
//                            updatedErrors.addAll(taskErrors);
//                        });
//
//                if (errors != null) {
//                    errors.addAll(updatedErrors);
//                } else {
//                    details.put("errors", updatedErrors);
//                }
//            }
//        }
    }


    private void startNext(DagStepDTO step) {
        DagInstanceComplexDTO dagInstanceComplexDTO = dagInstanceComplexService.selectOne(step.getDagInstance().getId());
        DAG<DagStepDTO> dag = DagUtil.buildDag(dagInstanceComplexDTO);
        Set<DagStepDTO> downstreamSteps = dag.outDegreeOf(step);
        if (CollectionUtils.isNotEmpty(downstreamSteps)) {
            downstreamSteps.forEach(s -> push(new Messages.StartStep(s)));
        } else {
            push(new Messages.CompleteDag(dagInstanceComplexDTO));
        }

//        PipelineExecution execution = step.getPipelineExecution();
//        List<StageExecution> downstreamStages = step.downstreamStages();
//        SyntheticStageOwner phase = step.getSyntheticStageOwner();
//
//        if (CollectionUtils.isNotEmpty(downstreamStages)) {
//            downstreamStages.forEach(s ->
//                    getQueue().push(new Messages.StartStage(s))
//            );
//        } else if (phase != null) {
//            getQueue().ensure(
//                    new Messages.ContinueParentStage(step.getParent(), phase),
//                    Duration.ZERO
//            );
//        } else {
//            getQueue().push(new Messages.CompleteExecution(execution));
//        }
    }
}

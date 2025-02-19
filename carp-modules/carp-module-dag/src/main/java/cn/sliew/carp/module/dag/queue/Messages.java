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
package cn.sliew.carp.module.dag.queue;

import cn.sliew.carp.framework.dag.service.dto.DagInstanceDTO;
import cn.sliew.carp.framework.dag.service.dto.DagStepDTO;
import cn.sliew.carp.module.dag.module.ExecutionStatus;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

public class Messages {

    /**
     * Messages used internally by the queueing system.
     */
    interface NamespaceAware {

        String getNamespace();
    }

    public interface DagLevel extends NamespaceAware {

        String getType();

        Long getDagId();
    }

    public interface StepLevel extends DagLevel {

        Long getStepId();
    }

    public interface TaskLevel extends StepLevel {

        Long getTaskId();
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("startExecution")
    public static class StartExecution implements DagLevel, Serializable {
        private final String namespace;
        private final String type;
        private final Long dagId;

        public StartExecution(DagInstanceDTO source) {
            this(source.getNamespace(), source.getDagConfig().getType(), source.getId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("rescheduleExecution")
    public static class RescheduleExecution implements DagLevel, Serializable {
        private final String namespace;
        private final String type;
        private final Long dagId;

        public RescheduleExecution(DagInstanceDTO source) {
            this(source.getNamespace(), source.getDagConfig().getType(), source.getId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("completeExecution")
    public static class CompleteExecution implements DagLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;

        public CompleteExecution(DagLevel source) {
            this(source.getNamespace(), source.getType(), source.getDagId());
        }

        public CompleteExecution(DagInstanceDTO source) {
            this(source.getNamespace(), source.getDagConfig().getType(), source.getId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("resumeExecution")
    public static class ResumeExecution implements DagLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;

        public ResumeExecution(DagInstanceDTO source) {
            this(source.getNamespace(), source.getDagConfig().getType(), source.getId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("cancelExecution")
    public static class CancelExecution implements DagLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private String user;
        private String reason;

        public CancelExecution(DagLevel source) {
            this(source, null, null);
        }

        public CancelExecution(DagLevel source, String user, String reason) {
            this(source.getNamespace(), source.getType(), source.getDagId(), user, reason);
        }

        public CancelExecution(DagInstanceDTO source) {
            this(source, null, null);
        }

        public CancelExecution(DagInstanceDTO source, String user, String reason) {
            this(source.getNamespace(), source.getDagConfig().getType(), source.getId(), user, reason);
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("startWaitingExecutions")
    public static class StartWaitingExecutions {
        private Long pipelineConfigId;
        private Boolean purgeQueue;
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("startStage")
    public static class StartStage implements StepLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;

        public StartStage(StepLevel source) {
            this(source, source.getStepId());
        }

        public StartStage(DagLevel source, Long stepId) {
            this(source.getNamespace(), source.getType(), source.getDagId(), stepId);
        }

        public StartStage(DagStepDTO source) {
            this(source.getDagInstance().getNamespace(), source.getDagInstance().getDagConfig().getType(), source.getDagInstance().getId(), source.getId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("continueParentStage")
    public static class ContinueParentStage implements StepLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;

        public ContinueParentStage(StepLevel source) {
            this(source.getNamespace(), source.getType(), source.getDagId(), source.getStepId());
        }

        public ContinueParentStage(DagStepDTO source) {
            this(source.getDagInstance().getNamespace(), source.getDagInstance().getDagConfig().getType(), source.getDagInstance().getId(), source.getId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("completeStage")
    public static class CompleteStage implements StepLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;

        public CompleteStage(StepLevel source) {
            this(source, source.getStepId());
        }

        public CompleteStage(DagLevel source, Long stepId) {
            this(source.getNamespace(), source.getType(), source.getDagId(), stepId);
        }

        public CompleteStage(DagStepDTO source) {
            this(source.getDagInstance().getNamespace(), source.getDagInstance().getDagConfig().getType(), source.getDagInstance().getId(), source.getId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("skipStage")
    public static class SkipStage implements StepLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;

        public SkipStage(StepLevel source) {
            this(source.getNamespace(), source.getType(), source.getDagId(), source.getStepId());
        }

        public SkipStage(DagStepDTO source) {
            this(source.getDagInstance().getNamespace(), source.getDagInstance().getDagConfig().getType(), source.getDagInstance().getId(), source.getId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("abortStage")
    public static class AbortStage implements StepLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;

        public AbortStage(StepLevel source) {
            this(source.getNamespace(), source.getType(), source.getDagId(), source.getStepId());
        }

        public AbortStage(DagStepDTO source) {
            this(source.getDagInstance().getNamespace(), source.getDagInstance().getDagConfig().getType(), source.getDagInstance().getId(), source.getId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("pauseStage")
    public static class PauseStage implements StepLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;

        public PauseStage(StepLevel source) {
            this(source, source.getStepId());
        }

        public PauseStage(DagLevel source, Long stepId) {
            this(source.getNamespace(), source.getType(), source.getDagId(), stepId);
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("restartStage")
    public static class RestartStage implements StepLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;
        private String user;

        public RestartStage(DagInstanceDTO source, Long stepId, String user) {
            this(source.getNamespace(), source.getDagConfig().getType(), source.getId(), stepId, user);
        }

        public RestartStage(DagStepDTO source, String user) {
            this(source.getDagInstance().getNamespace(), source.getDagInstance().getDagConfig().getType(), source.getDagInstance().getId(), source.getId(), user);
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("resumeStage")
    public static class ResumeStage implements StepLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;

        public ResumeStage(DagLevel source, Long stageId) {
            this(source.getNamespace(), source.getType(), source.getDagId(), stageId);
        }

        public ResumeStage(DagStepDTO source) {
            this(source.getDagInstance().getNamespace(), source.getDagInstance().getDagConfig().getType(), source.getDagInstance().getId(), source.getId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("cancelStage")
    public static class CancelStage implements StepLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;

        public CancelStage(StepLevel source) {
            this(source.getNamespace(), source.getType(), source.getDagId(), source.getStepId());
        }

        public CancelStage(DagStepDTO source) {
            this(source.getDagInstance().getNamespace(), source.getDagInstance().getDagConfig().getType(), source.getDagInstance().getId(), source.getId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("startTask")
    public static class StartTask implements TaskLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;
        private final Long taskId;

        public StartTask(DagLevel source, Long stepId, Long taskId) {
            this(source.getNamespace(), source.getType(), source.getDagId(),
                    stepId, taskId);
        }

        public StartTask(StepLevel source, Long taskId) {
            this(source, source.getStepId(), taskId);
        }

        public StartTask(DagStepDTO source, Long taskId) {
            this(source.getDagInstance().getNamespace(), source.getDagInstance().getDagConfig().getType(),
                    source.getDagInstance().getId(), source.getId(), taskId);
        }

        public StartTask(DagStepDTO source, TaskExecution task) {
            this(source.getDagInstance().getNamespace(), source.getDagInstance().getDagConfig().getType(),
                    source.getDagInstance().getId(), source.getId(), task.getId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("runTask")
    public static class RunTask implements TaskLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;
        private final Long taskId;
        private Class<? extends Task> taskType;

        public RunTask(TaskLevel source, Class<? extends Task> taskType) {
            this(source.getNamespace(), source.getType(), source.getDagId(),
                    source.getStepId(), source.getTaskId(), taskType);
        }

        public RunTask(StepLevel source, Long taskId, Class<? extends Task> taskType) {
            this(source.getNamespace(), source.getType(), source.getDagId(),
                    source.getStepId(), taskId, taskType);
        }

        public RunTask(DagLevel source, Long stepId, Long taskId, Class<? extends Task> taskType) {
            this(source.getNamespace(), source.getType(), source.getDagId(),
                    stepId, taskId, taskType);
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("completeTask")
    public static class CompleteTask implements TaskLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;
        private final Long taskId;
        private ExecutionStatus status;
        private ExecutionStatus originalStatus;

        public CompleteTask(TaskLevel source, ExecutionStatus status) {
            this(source, status, null);
        }

        public CompleteTask(TaskLevel source, ExecutionStatus status, ExecutionStatus originalStatus) {
            this(source.getNamespace(), source.getType(), source.getDagId(),
                    source.getStepId(), source.getTaskId(), status, originalStatus);
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("pauseTask")
    public static class PauseTask implements TaskLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;
        private final Long taskId;

        public PauseTask(TaskLevel source) {
            this(source.getNamespace(), source.getType(), source.getDagId(),
                    source.getStepId(), source.getTaskId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("resumeTask")
    public static class ResumeTask implements TaskLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;
        private final Long taskId;

        public ResumeTask(StepLevel source, Long taskId) {
            this(source.getNamespace(), source.getType(), source.getDagId(),
                    source.getStepId(), taskId);
        }
    }

    public static abstract class ConfigurationError implements DagLevel {

    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("invalidExecutionId")
    public static class InvalidExecutionId extends ConfigurationError {
        private final String namespace;
        private final String type;
        private final Long dagId;

        public InvalidExecutionId(DagLevel source) {
            this(source.getNamespace(), source.getType(), source.getDagId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("invalidStageId")
    public static class InvalidStageId extends ConfigurationError implements StepLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;

        public InvalidStageId(StepLevel source) {
            this(source.getNamespace(), source.getType(), source.getDagId(), source.getStepId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("invalidTaskId")
    public static class InvalidTaskId extends ConfigurationError implements TaskLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;
        private final Long taskId;

        public InvalidTaskId(TaskLevel source) {
            this(source.getNamespace(), source.getType(), source.getDagId(), source.getStepId(), source.getTaskId());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("invalidTaskType")
    public static class InvalidTaskType extends ConfigurationError implements StepLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;
        private String className;

        public InvalidTaskType(StepLevel source, String className) {
            this(source.getNamespace(), source.getType(), source.getDagId(), source.getStepId(), className);
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonTypeName("noDownstreamTasks")
    public static class NoDownstreamTasks extends ConfigurationError implements TaskLevel {
        private final String namespace;
        private final String type;
        private final Long dagId;
        private final Long stepId;
        private final Long taskId;

        public NoDownstreamTasks(TaskLevel source) {
            this(source.getNamespace(), source.getType(), source.getDagId(), source.getStepId(), source.getTaskId());
        }
    }

}

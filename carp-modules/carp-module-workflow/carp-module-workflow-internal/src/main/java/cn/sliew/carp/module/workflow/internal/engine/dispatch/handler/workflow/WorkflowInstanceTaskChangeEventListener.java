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

package cn.sliew.carp.module.workflow.internal.engine.dispatch.handler.workflow;

import cn.sliew.carp.framework.common.dict.workflow.WorkflowExecuteType;
import cn.sliew.carp.framework.common.dict.workflow.WorkflowInstanceEvent;
import cn.sliew.carp.framework.common.dict.workflow.WorkflowInstanceState;
import cn.sliew.carp.framework.dag.algorithm.DAG;
import cn.sliew.carp.module.workflow.api.engine.domain.instance.WorkflowExecutionGraph;
import cn.sliew.carp.module.workflow.api.engine.domain.instance.WorkflowInstance;
import cn.sliew.carp.module.workflow.api.engine.domain.instance.WorkflowTaskInstance;
import cn.sliew.carp.module.workflow.api.service.convert.WorkflowExecutionGraphConvert;
import cn.sliew.carp.module.workflow.internal.engine.dispatch.event.WorkflowInstanceEventDTO;
import cn.sliew.carp.module.workflow.internal.executor.WorkflowInstanceExecutorManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class WorkflowInstanceTaskChangeEventListener extends AbstractWorkflowInstanceEventListener {

    @Autowired
    private WorkflowInstanceExecutorManager workflowInstanceExecutorManager;

    @Override
    public WorkflowInstanceEvent getType() {
        return WorkflowInstanceEvent.PROCESS_TASK_CHANGE;
    }

    @Override
    protected CompletableFuture handleEventAsync(WorkflowInstanceEventDTO event) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> run(event.getWorkflowInstanceId()));
        future.whenComplete(((unused, throwable) -> {
            if (throwable != null) {
                onFailure(event.getWorkflowInstanceId(), throwable);
            }
        }));
        return future;
    }

    private void run(Long workflowInstanceId) {
        WorkflowInstance workflowInstance = workflowInstanceService.getGraph(workflowInstanceId);
        if (WorkflowInstanceState.FAILURE == workflowInstance.getStatus()) {
            return;
        }

        DAG<WorkflowTaskInstance> dag = WorkflowExecutionGraphConvert.INSTANCE.toDto(workflowInstance.getGraph());
        // 检测所有任务的状态，如果有一个失败，则失败。如果都执行成功，则成功
        int successTaskCount = 0;
        boolean isAnyFailure = false;
        String anyFailureMessage = null;
        for (WorkflowTaskInstance taskInstance : dag.nodes()) {
            if (taskInstance.getStatus().isEnd()) {
                if (taskInstance.getStatus().isFailure()) {
                    isAnyFailure = true;
//                    anyFailureMessage = dagStepDTO.getMessage();
                    break;
                }
                if (taskInstance.getStatus().isSuccess()) {
                    successTaskCount++;
                }
            }
        }

        if (successTaskCount == dag.nodes().size()) {
            stateMachine.onSuccess(workflowInstanceService.get(workflowInstanceId));
            return;
        }

        if (isAnyFailure) {
            onFailure(workflowInstanceId, new Exception(anyFailureMessage));
            return;
        }

        // 继续执行剩余节点
        workflowInstanceExecutorManager.execute(WorkflowExecuteType.EXECUTE, workflowInstance, dag);
    }

}

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
package cn.sliew.carp.module.orca.core.persistence.sql;

import cn.sliew.carp.framework.dag.service.*;
import cn.sliew.carp.framework.dag.service.dto.orca.CarpDagOrcaPipelineDTO;
import cn.sliew.carp.framework.dag.service.param.orca.CarpDagOrcaPipelineAddParam;
import cn.sliew.carp.framework.dag.service.param.orca.CarpDagOrcaPipelineStageAddParam;
import cn.sliew.carp.framework.dag.service.param.orca.CarpDagOrcaPipelineStageUpdateParam;
import cn.sliew.carp.framework.dag.service.param.orca.CarpDagOrcaPipelineUpdateParam;
import cn.sliew.carp.module.orca.spinnaker.api.model.ExecutionStatus;
import cn.sliew.carp.module.orca.spinnaker.api.model.ExecutionType;
import cn.sliew.carp.module.orca.spinnaker.api.model.pipeline.PipelineExecution;
import cn.sliew.carp.module.orca.spinnaker.api.model.pipeline.PipelineExecutionImpl;
import cn.sliew.carp.module.orca.spinnaker.api.model.stage.StageExecution;
import cn.sliew.carp.module.orca.spinnaker.api.model.stage.StageExecutionImpl;
import cn.sliew.carp.module.orca.spinnaker.api.persistence.ExecutionNotFoundException;
import cn.sliew.carp.module.orca.spinnaker.api.persistence.ExecutionRepository;
import cn.sliew.milky.common.util.JacksonUtil;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SqlExecutionRepository implements ExecutionRepository {

    private CarpDagOrcaPipelineService carpDagOrcaPipelineService;
    private DagConfigService dagConfigService;
    private DagConfigStepService dagConfigStepService;
    private DagConfigLinkService dagConfigLinkService;


    public SqlExecutionRepository(CarpDagOrcaPipelineService carpDagOrcaPipelineService) {
        this.carpDagOrcaPipelineService = carpDagOrcaPipelineService;
    }

    @Override
    public PipelineExecution retrieve(ExecutionType type, Long id) throws ExecutionNotFoundException {
        CarpDagOrcaPipelineDTO carpDagOrcaPipelineDTO = carpDagOrcaPipelineService.get(id);
        PipelineExecution execution = JacksonUtil.toObject(carpDagOrcaPipelineDTO.getBody(), PipelineExecution.class);
        ((PipelineExecutionImpl) execution).setId(id);
        return execution;
    }

    @Override
    public Observable<PipelineExecution> retrieve(ExecutionType type) {
        return Observable.fromArray();
    }

    @Override
    public Long store(PipelineExecution execution) {
        // upsert PipelineExecution -> CarpDagOrcaPipelineDTO
        if (Objects.nonNull(execution.getId())) {
            CarpDagOrcaPipelineUpdateParam updateParam = new CarpDagOrcaPipelineUpdateParam();
            updateParam.setId(execution.getId());
            updateParam.setUuid(execution.getUuid());
            updateParam.setNamespace(execution.getNamespace());
            updateParam.setType(execution.getType().name());
            updateParam.setConfigId(execution.getPipelineConfigId());
            updateParam.setName(execution.getName());
            updateParam.setStatus(execution.getStatus().name());
            updateParam.setBuildTime(execution.getBuildTime().toEpochMilli());
            Optional.ofNullable(execution.getStartTime()).ifPresent(startTime -> updateParam.setStartTime(startTime.toEpochMilli()));
            Optional.ofNullable(execution.getEndTime()).ifPresent(endTime -> updateParam.setEndTime(endTime.toEpochMilli()));
            updateParam.setCanceled(execution.isCanceled());
            updateParam.setBody(JacksonUtil.toJsonNode(execution));
            updateParam.setRemark(execution.getRemark());
            carpDagOrcaPipelineService.update(updateParam);
            return execution.getId();
        } else {
            CarpDagOrcaPipelineAddParam addParam = new CarpDagOrcaPipelineAddParam();
            addParam.setUuid(execution.getUuid());
            addParam.setNamespace(execution.getNamespace());
            addParam.setType(execution.getType().name());
            addParam.setConfigId(execution.getPipelineConfigId());
            addParam.setName(execution.getName());
            addParam.setBody(JacksonUtil.toJsonNode(execution));
            addParam.setRemark(execution.getRemark());
            return carpDagOrcaPipelineService.add(addParam);
        }
    }

    @Override
    public Long storeStage(StageExecution stage) {
        try {
            carpDagOrcaPipelineService.getStage(stage.getId());
            // update
            CarpDagOrcaPipelineStageUpdateParam param = new CarpDagOrcaPipelineStageUpdateParam();
            param.setId(stage.getId());
            param.setUuid(stage.getUuid());
            param.setPipelineId(stage.getPipelineExecution().getId());
            param.setStatus(stage.getStatus().name());
            param.setBody(JacksonUtil.toJsonNode(stage));
            carpDagOrcaPipelineService.updateStage(param);
            return stage.getId();
        } catch (NullPointerException ignored) {
            // insert
            return addStage(stage);
        }
    }

    @Override
    public Long addStage(StageExecution stage) {
        CarpDagOrcaPipelineStageAddParam addParam = new CarpDagOrcaPipelineStageAddParam();
        addParam.setPipelineId(stage.getPipelineExecution().getId());
        addParam.setStatus(stage.getStatus().name());
        addParam.setBody(JacksonUtil.toJsonNode(stage));
        Long id = carpDagOrcaPipelineService.addStage(addParam);
        ((StageExecutionImpl) stage).setId(id);
        return id;
    }

    @Override
    public void removeStage(PipelineExecution execution, Long stageId) {
        carpDagOrcaPipelineService.deleteStage(stageId);
    }

    @Override
    public void updateStageContext(StageExecution stage) {
        storeStage(stage);
    }

    @Override
    public boolean isCanceled(ExecutionType type, Long id) {
        CarpDagOrcaPipelineDTO carpDagOrcaPipelineDTO = carpDagOrcaPipelineService.get(id);
        return carpDagOrcaPipelineDTO.getCanceled();
    }

    @Override
    public void cancel(ExecutionType type, Long id) {

    }

    @Override
    public void cancel(ExecutionType type, Long id, String user, String reason) {

    }

    @Override
    public void pause(ExecutionType type, Long id, String user) {

    }

    @Override
    public void resume(ExecutionType type, Long id, String user) {

    }

    @Override
    public void resume(ExecutionType type, Long id, String user, boolean ignoreCurrentStatus) {

    }

    @Override
    public void updateStatus(ExecutionType type, Long id, ExecutionStatus status) {
        PipelineExecutionImpl execution = (PipelineExecutionImpl) retrieve(type, id);
        execution.setStatus(status);
        store(execution);
    }

    @Override
    public void delete(ExecutionType type, Long id) {
        carpDagOrcaPipelineService.delete(id);
    }

    @Override
    public void delete(ExecutionType type, List<Long> ids) {
        carpDagOrcaPipelineService.deleteBatch(ids);
    }

    @Override
    public boolean hasExecution(ExecutionType type, Long id) {
        return false;
    }

    @Override
    public List<Long> retrieveAllExecutionIds(ExecutionType type) {
        return null;
    }
}

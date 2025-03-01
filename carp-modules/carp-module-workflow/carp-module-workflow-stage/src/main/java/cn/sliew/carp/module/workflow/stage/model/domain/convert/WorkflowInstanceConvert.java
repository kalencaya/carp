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
package cn.sliew.carp.module.workflow.stage.model.domain.convert;

import cn.sliew.carp.framework.common.convert.BaseConvert;
import cn.sliew.carp.framework.dag.service.dto.DagConfigComplexDTO;
import cn.sliew.carp.framework.dag.service.dto.DagInstanceDTO;
import cn.sliew.carp.module.workflow.stage.model.domain.instance.WorkflowInstance;
import cn.sliew.carp.module.workflow.stage.model.domain.instance.WorkflowInstanceBody;
import cn.sliew.milky.common.util.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.BeanUtils;

import java.util.Map;
import java.util.Objects;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkflowInstanceConvert extends BaseConvert<DagInstanceDTO, WorkflowInstance> {
    WorkflowInstanceConvert INSTANCE = Mappers.getMapper(WorkflowInstanceConvert.class);

    @Override
    default DagInstanceDTO toDo(WorkflowInstance dto) {
        DagInstanceDTO entity = new DagInstanceDTO();
        BeanUtils.copyProperties(dto, entity);
        if (dto.getDefinition() != null) {
            DagConfigComplexDTO dagConfigComplexDTO = new DagConfigComplexDTO();
            dagConfigComplexDTO.setId(dto.getDefinition().getId());
            entity.setDagConfig(dagConfigComplexDTO);
        }
        if (Objects.nonNull(dto.getBody())) {
            entity.setBody(JacksonUtil.toJsonNode(dto.getBody()));
        }
        if (Objects.nonNull(dto.getInputs())) {
            entity.setInputs(JacksonUtil.toJsonNode(dto.getInputs()));
        }
        if (Objects.nonNull(dto.getOutputs())) {
            entity.setOutputs(JacksonUtil.toJsonNode(dto.getOutputs()));
        }
        return entity;
    }

    @Override
    default WorkflowInstance toDto(DagInstanceDTO entity) {
        WorkflowInstance dto = new WorkflowInstance();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getDagConfig() != null) {
            dto.setDefinition(WorkflowDefinitionConvert.INSTANCE.toDto(entity.getDagConfig()));
        }
        if (Objects.nonNull(entity.getBody())
                && entity.getBody().isNull() == false
                && entity.getBody().isEmpty() == false) {
            dto.setBody(JacksonUtil.toObject(entity.getBody(), WorkflowInstanceBody.class));
        }
        if (Objects.nonNull(entity.getInputs())
                && entity.getInputs().isNull() == false
                && entity.getInputs().isEmpty() == false) {
            dto.setInputs(JacksonUtil.toObject(entity.getInputs(), new TypeReference<Map<String, Object>>() {}));
        } else {
            dto.setInputs(Maps.newHashMap());
        }
        if (Objects.nonNull(entity.getOutputs())
                && entity.getOutputs().isNull() == false
                && entity.getOutputs().isEmpty() == false) {
            dto.setOutputs(JacksonUtil.toObject(entity.getOutputs(), new TypeReference<Map<String, Object>>() {}));
        } else {
            dto.setOutputs(Maps.newHashMap());
        }
        return dto;
    }
}

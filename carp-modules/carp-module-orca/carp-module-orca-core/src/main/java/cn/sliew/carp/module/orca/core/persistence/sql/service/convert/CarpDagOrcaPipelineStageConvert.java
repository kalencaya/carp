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
package cn.sliew.carp.module.orca.core.persistence.sql.service.convert;

import cn.sliew.carp.framework.common.convert.BaseConvert;
import cn.sliew.carp.framework.dag.service.dto.DagStepDTO;
import cn.sliew.carp.module.orca.core.persistence.sql.service.dto.CarpDagOrcaPipelineStageDTO;
import cn.sliew.milky.common.util.JacksonUtil;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.BeanUtils;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarpDagOrcaPipelineStageConvert extends BaseConvert<DagStepDTO, CarpDagOrcaPipelineStageDTO> {
    CarpDagOrcaPipelineStageConvert INSTANCE = Mappers.getMapper(CarpDagOrcaPipelineStageConvert.class);

    @Override
    default DagStepDTO toDo(CarpDagOrcaPipelineStageDTO dto) {
        throw new UnsupportedOperationException();
    }

    @Override
    default CarpDagOrcaPipelineStageDTO toDto(DagStepDTO entity) {
        CarpDagOrcaPipelineStageDTO dto = new CarpDagOrcaPipelineStageDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getStartTime() != null) {
            dto.setStartTime(entity.getStartTime().toInstant());
        }
        if (entity.getEndTime() != null) {
            dto.setEndTime(entity.getEndTime().toInstant());
        }
        if (entity.getBody() != null) {
            dto.setBody(JacksonUtil.toObject(entity.getBody(), CarpDagOrcaPipelineStageDTO.CarpDagOrcaPipelineStageBody.class));
        }
        return dto;
    }

}

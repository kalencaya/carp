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
package cn.sliew.carp.module.scheduler.service.convert;

import cn.sliew.carp.framework.common.convert.BaseConvert;
import cn.sliew.carp.module.scheduler.repository.entity.ScheduleJobConfig;
import cn.sliew.carp.module.scheduler.repository.entity.ScheduleJobConfigVO;
import cn.sliew.carp.module.scheduler.service.dto.ScheduleJobConfigDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.BeanUtils;

@Mapper
public interface ScheduleJobConfigConvert extends BaseConvert<ScheduleJobConfig, ScheduleJobConfigDTO> {
    ScheduleJobConfigConvert INSTANCE = Mappers.getMapper(ScheduleJobConfigConvert.class);

    @Override
    default ScheduleJobConfigDTO toDto(ScheduleJobConfig entity) {
        ScheduleJobConfigDTO dto = new ScheduleJobConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity instanceof ScheduleJobConfigVO) {
            ScheduleJobConfigVO configVO = (ScheduleJobConfigVO) entity;
            dto.setJobGroup(ScheduleJobGroupConvert.INSTANCE.toDto(configVO.getJobGroup()));
        }
        return dto;
    }
}

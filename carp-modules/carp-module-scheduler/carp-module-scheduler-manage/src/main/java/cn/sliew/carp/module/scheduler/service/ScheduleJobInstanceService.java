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
package cn.sliew.carp.module.scheduler.service;

import cn.sliew.carp.framework.common.dict.schedule.CarpScheduleStatus;
import cn.sliew.carp.framework.common.model.PageResult;
import cn.sliew.carp.module.scheduler.repository.entity.ScheduleJobInstance;
import cn.sliew.carp.module.scheduler.service.dto.ScheduleJobInstanceDTO;
import cn.sliew.carp.module.scheduler.service.param.ScheduleJobInstanceAddParam;
import cn.sliew.carp.module.scheduler.service.param.ScheduleJobInstanceListParam;
import cn.sliew.carp.module.scheduler.service.param.ScheduleJobInstancePageParam;
import cn.sliew.carp.module.scheduler.service.param.ScheduleJobInstanceUpdateParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

public interface ScheduleJobInstanceService extends IService<ScheduleJobInstance> {

    PageResult<ScheduleJobInstanceDTO> list(ScheduleJobInstancePageParam param);

    List<ScheduleJobInstanceDTO> listAll(ScheduleJobInstanceListParam param);

    ScheduleJobInstanceDTO get(Long id);

    boolean add(ScheduleJobInstanceAddParam param);

    boolean update(ScheduleJobInstanceUpdateParam param);

    boolean updateStatus(Long id, CarpScheduleStatus status);

    boolean delete(Long id);

    boolean deleteBatch(Collection<Long> ids);
}

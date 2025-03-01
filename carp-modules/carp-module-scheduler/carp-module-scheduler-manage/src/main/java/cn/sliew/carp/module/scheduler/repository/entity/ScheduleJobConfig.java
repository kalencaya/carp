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
package cn.sliew.carp.module.scheduler.repository.entity;

import cn.sliew.carp.framework.common.dict.schedule.CarpScheduleEngineType;
import cn.sliew.carp.framework.common.dict.schedule.CarpScheduleJobType;
import cn.sliew.carp.framework.common.dict.schedule.CarpScheduleType;
import cn.sliew.carp.framework.mybatis.entity.BaseAuditDO;
import cn.sliew.carp.module.scheduler.executor.api.dict.CarpScheduleExecuteType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "carp_schedule_job_config", resultMap = "ScheduleJobConfigMap")
public class ScheduleJobConfig extends BaseAuditDO {

    @TableField("job_group_id")
    private Long jobGroupId;

    @TableField(value = "job_group", exist = false)
    private ScheduleJobGroup jobGroup;

    @TableField("`type`")
    private CarpScheduleType type;

    @TableField("`engine_type`")
    private CarpScheduleEngineType engineType;

    @TableField("`job_type`")
    private CarpScheduleJobType jobType;

    @TableField("`execute_type`")
    private CarpScheduleExecuteType executeType;

    @TableField("`name`")
    private String name;

    @TableField("`handler`")
    private String handler;

    @TableField("remark")
    private String remark;
}

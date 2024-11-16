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

package cn.sliew.carp.module.http.sync.framework.model;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.sliew.carp.module.http.sync.framework.util.GradientUtil;
import cn.sliew.carp.module.http.sync.framework.util.SyncOffsetHelper;
import org.apache.pekko.japi.Pair;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public interface SplitManager {

    List<Duration> getGradients();

    default boolean supportSplit(String startSyncOffset, String endSyncOffset, Duration gradient) {
        LocalDateTime startTime = LocalDateTime.parse(startSyncOffset, DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
        LocalDateTime endTime = LocalDateTime.parse(endSyncOffset, DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
        return SyncOffsetHelper.supportSplit(startTime, endTime, gradient);
    }

    default List<Pair<String, String>> split(String startSyncOffset, String endSyncOffset, Duration gradient, int total) {
        LocalDateTime startTime = LocalDateTime.parse(startSyncOffset, DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
        LocalDateTime endTime = LocalDateTime.parse(endSyncOffset, DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
        return SyncOffsetHelper.split(startTime, endTime, gradient, total).stream().map(pair -> {
            return Pair.create(DateUtil.format(pair.first(), DatePattern.NORM_DATETIME_PATTERN), DateUtil.format(pair.first(), DatePattern.NORM_DATETIME_PATTERN));
        }).collect(Collectors.toUnmodifiableList());
    }

    default Duration getBackoffGradient() {
        return GradientUtil.MIN_GRADIENT;
    }
}

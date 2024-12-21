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
package cn.sliew.carp.module.plugin.plugin.update;

import lombok.Data;
import org.apache.commons.lang3.compare.ObjectToStringComparator;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
public class RemotePluginInfo implements Comparable<RemotePluginInfo>, Serializable {

    private String id;
    private String projectUrl;
    private String name;
    private String remark;
    private String provider;
    private List<PluginRelease> releases;

    @Override
    public int compareTo(RemotePluginInfo other) {
        return Objects.compare(this, other, ObjectToStringComparator.INSTANCE);
    }
}

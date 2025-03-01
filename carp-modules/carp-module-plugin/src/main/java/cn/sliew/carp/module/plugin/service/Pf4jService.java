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
package cn.sliew.carp.module.plugin.service;

import cn.sliew.carp.framework.common.model.PageParam;
import cn.sliew.carp.framework.common.model.PageResult;
import cn.sliew.carp.module.plugin.plugin.update.PluginRepositoryInfo;
import cn.sliew.carp.module.plugin.plugin.update.RemotePluginInfo;
import cn.sliew.carp.module.plugin.service.param.CarpRemotePluginInfoPageParam;
import org.pf4j.PluginDescriptor;

import java.nio.file.Path;
import java.util.List;

public interface Pf4jService {

    List<PluginRepositoryInfo> listRemoteRepository();

    PageResult<RemotePluginInfo> pageRemotePluginInfo(CarpRemotePluginInfoPageParam param);

    List<RemotePluginInfo> listRemotePluginInfo(CarpRemotePluginInfoPageParam param);

    PageResult<PluginDescriptor> page(PageParam param);

    List<PluginDescriptor> listAll();

    PluginDescriptor get(String pluginId);

    String loadPlugin(Path path);

    boolean unloadPlugin(String pluginId);

    boolean enablePlugin(String pluginId);

    boolean disablePlugin(String pluginId);

    <EP> List<EP> getExtensions(Class<EP> clazz);

    <EP> List<EP> getExtensions(Class<EP> clazz, String pluginId);

    void testExtension();
}

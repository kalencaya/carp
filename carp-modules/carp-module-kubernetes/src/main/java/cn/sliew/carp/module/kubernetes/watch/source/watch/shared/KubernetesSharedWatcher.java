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

package cn.sliew.carp.module.kubernetes.watch.source.watch.shared;

import cn.sliew.scaleph.kubernetes.watch.watch.WatchCallbackHandler;

import java.util.concurrent.Executor;

/**
 * The interface for the Kubernetes shared watcher.
 */
public interface KubernetesSharedWatcher<T> extends AutoCloseable {

    Watch watch(String name, WatchCallbackHandler<T> handler, Executor executor);

    @Override
    void close();

    interface Watch extends AutoCloseable {
        @Override
        void close();
    }
}

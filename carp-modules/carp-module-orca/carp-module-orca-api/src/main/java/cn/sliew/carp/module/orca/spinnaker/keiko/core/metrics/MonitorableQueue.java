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
package cn.sliew.carp.module.orca.spinnaker.keiko.core.metrics;

import cn.sliew.carp.module.orca.spinnaker.keiko.core.Message;
import cn.sliew.carp.module.orca.spinnaker.keiko.core.Queue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Predicate;

public interface MonitorableQueue extends Queue {

    EventPublisher getPublisher();

    QueueState readState();

    boolean containsMessage(Predicate<Message> predicate);

    default void fire(QueueEvent event) {
        getPublisher().publishEvent(event);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueState {

        public QueueState(int depth, int ready, int unacked) {
            this(depth, ready, unacked, 0, 0);
        }

        /**
         * Number of messages currently queued for delivery including any not yet due.
         */
        private Integer depth;
        /**
         * Number of messages ready for delivery.
         */
        private Integer ready;
        /**
         * Number of messages currently being processed but not yet acknowledged.
         */
        private Integer unacked;
        /**
         * Number of messages neither queued or in-process.
         *
         * Some implementations may not have any way to implement this metric. It is
         * only intended for alerting leaks.
         */
        private Integer orphaned = 0;
        /**
         * Difference between number of known message hashes and number of de-dupe
         * hashes plus in-process messages.
         *
         * Some implementations may not have any way to implement this metric. It is
         * only intended for alerting leaks.
         */
        private Integer hashDrift = 0;

    }
}

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
package cn.sliew.carp.module.orca.spinnaker.orca.core.exceptions;

import com.google.common.base.Strings;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ExceptionHandler.
 */
public interface ExceptionHandler {

    boolean handles(Exception e);

    Response handle(String taskName, Exception exception);

    class Response {
        private final Date timestamp = new Date();
        private final String exceptionType;
        private final String operation;
        private final Map<String, Object> details;
        private final boolean shouldRetry;

        public Response(
                String exceptionType,
                String operation,
                Map<String, Object> responseDetails,
                boolean shouldRetry) {
            this.exceptionType = exceptionType;
            this.operation = operation;
            this.details = responseDetails == null ? new HashMap<>() : new HashMap<>(responseDetails);
            this.shouldRetry = shouldRetry;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public String getExceptionType() {
            return exceptionType;
        }

        public String getOperation() {
            return operation;
        }

        public Map<String, Object> getDetails() {
            return details;
        }

        public boolean isShouldRetry() {
            return shouldRetry;
        }

        public boolean getShouldRetry() {
            return shouldRetry;
        }

        @Override
        public String toString() {
            return "Response{"
                    + "timestamp="
                    + timestamp
                    + ", exceptionType='"
                    + exceptionType
                    + '\''
                    + ", operation='"
                    + operation
                    + '\''
                    + ", details="
                    + details
                    + '}';
        }
    }

    static Map<String, Object> responseDetails(String error) {
        return responseDetails(error, null);
    }

    static Map<String, Object> responseDetails(String error, List<String> errors) {
        Map<String, Object> details = new HashMap<>();
        details.put("error", error);
        if (errors == null) {
            errors = Collections.emptyList();
        }

        details.put(
                "errors",
                errors.stream().filter(x -> !Strings.isNullOrEmpty(x)).collect(Collectors.toList()));
        return details;
    }
}

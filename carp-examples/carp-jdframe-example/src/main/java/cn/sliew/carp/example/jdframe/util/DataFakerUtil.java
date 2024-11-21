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

package cn.sliew.carp.example.jdframe.util;

import cn.sliew.carp.example.jdframe.User;
import net.datafaker.Faker;
import net.datafaker.transformations.Field;
import net.datafaker.transformations.JavaObjectTransformer;
import net.datafaker.transformations.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum DataFakerUtil {
    ;

    private static final Faker FAKER = new Faker();
    private static final JavaObjectTransformer J_TRANSFORMER = new JavaObjectTransformer();

    public static Schema<Object, ?> userDataSchema() {
        return Schema.of(
                Field.field("id", () -> FAKER.number().positive()),
                Field.field("name", () -> FAKER.name().fullName()),
                Field.field("gender", () -> FAKER.gender().types()),
                Field.field("age", () -> FAKER.number().numberBetween(1, 100)),
                Field.field("groupIndex", () -> FAKER.number().numberBetween(1, 3)),
                Field.field("groupName", () -> FAKER.color().name())
        );
    }

    public static User generate() {
        return (User) J_TRANSFORMER.apply(User.class, userDataSchema());
    }

    public static List<User> generateList(Integer count) {
        return IntStream.range(0, count).mapToObj(index -> generate()).collect(Collectors.toList());
    }
}

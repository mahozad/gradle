/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.process;

import org.gradle.internal.instrumentation.api.annotations.BytecodeUpgrade;

import java.util.List;

class JvmArgsAdapter {
    @BytecodeUpgrade
    static List<String> getJvmArgs(JavaForkOptions options) {
        return options.getJvmArgs().get();
    }

    @BytecodeUpgrade
    static void setJvmArgs(JavaForkOptions options, List<String> arguments) {
        options.getJvmArgs().addAll(arguments);
    }

    @BytecodeUpgrade
    static void setJvmArgs(JavaForkOptions options, Iterable<?> arguments) {
        for (Object argument : arguments) {
            options.getJvmArgs().add(argument.toString());
        }
    }
}

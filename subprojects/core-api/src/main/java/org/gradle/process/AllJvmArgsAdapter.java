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

import org.apache.commons.lang.NotImplementedException;
import org.gradle.internal.instrumentation.api.annotations.BytecodeUpgrade;

import java.util.List;

class AllJvmArgsAdapter {
    @BytecodeUpgrade
    static List<String> getAllJvmArgs(JavaForkOptions options) {
        return options.getAllJvmArgs().get();
    }

    @BytecodeUpgrade
    @SuppressWarnings("DoNotCallSuggester")
    static void setAllJvmArgs(JavaForkOptions options, List<String> arguments) {
        throw new NotImplementedException("Not implemented yet");
    }

    @BytecodeUpgrade
    @SuppressWarnings("DoNotCallSuggester")
    static void setAllJvmArgs(JavaForkOptions options, Iterable<?> arguments) {
        throw new NotImplementedException("Not implemented yet");
    }
}

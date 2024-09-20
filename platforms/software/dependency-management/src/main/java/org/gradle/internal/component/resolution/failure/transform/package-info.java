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

/**
 * This package contains types for capturing data about the chain of variant transformations that
 * are available to satisfy an artifact selection request.
 * <p>
 * These types are a more lightweight representation of the data in {@link org.gradle.api.internal.artifacts.transform.TransformedVariant TransformedVariant},
 * that will not pose problems to serialization in {@link org.gradle.internal.operations.BuildOperation BuildOperation}s and traces.  They
 * contain all the interesting data about these transformation chains necessary to analyze a failure and describe it
 * using a {@link org.gradle.internal.component.resolution.failure.describer.ResolutionFailureDescriber ResolutionFailureDescriber}.
 */
@org.gradle.api.NonNullApi
package org.gradle.internal.component.resolution.failure.transform;

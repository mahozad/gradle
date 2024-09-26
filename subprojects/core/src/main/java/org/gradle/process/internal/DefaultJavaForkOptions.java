/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.process.internal;

import org.gradle.api.Action;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.internal.file.FileCollectionFactory;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.internal.file.PathToFileResolver;
import org.gradle.internal.jvm.Jvm;
import org.gradle.process.CommandLineArgumentProvider;
import org.gradle.process.JavaDebugOptions;
import org.gradle.process.JavaForkOptions;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.gradle.process.internal.util.MergeOptionsUtil.containsAll;
import static org.gradle.process.internal.util.MergeOptionsUtil.getHeapSizeMb;
import static org.gradle.process.internal.util.MergeOptionsUtil.normalized;

public class DefaultJavaForkOptions extends DefaultProcessForkOptions implements JavaForkOptionsInternal {
    private final FileCollectionFactory fileCollectionFactory;
    private final ObjectFactory objectFactory;
    private List<String> jvmArgs;
    private final ListProperty<CommandLineArgumentProvider> jvmArgumentProviders;
    private Map<String, Object> systemProperties;
    private final ConfigurableFileCollection bootstrapClasspath;
    private final Property<String> minHeapSize;
    private final Property<String> maxHeapSize;
    private final Property<String> defaultCharacterEncoding;
    private final Property<Boolean> enableAssertions;
    private final JavaDebugOptions debugOptions;
    private Iterable<?> extraJvmArgs;

    @Inject
    public DefaultJavaForkOptions(ObjectFactory objectFactory, PathToFileResolver resolver, FileCollectionFactory fileCollectionFactory, JavaDebugOptions debugOptions) {
        super(resolver);
        this.objectFactory = objectFactory;
        this.fileCollectionFactory = fileCollectionFactory;
        this.jvmArgs = new ArrayList<>();
        this.jvmArgumentProviders = objectFactory.listProperty(CommandLineArgumentProvider.class);
        this.systemProperties = new LinkedHashMap<>();
        this.bootstrapClasspath = objectFactory.fileCollection();
        this.minHeapSize = objectFactory.property(String.class);
        this.maxHeapSize = objectFactory.property(String.class);
        this.debugOptions = debugOptions;
        JvmOptions emptyJvmOptions = new JvmOptions(fileCollectionFactory, objectFactory.newInstance(DefaultJavaDebugOptions.class, objectFactory));
        this.defaultCharacterEncoding = objectFactory.property(String.class).convention(emptyJvmOptions.getDefaultCharacterEncoding());
        this.enableAssertions = objectFactory.property(Boolean.class).convention(emptyJvmOptions.getEnableAssertions());
        this.debugOptions.getEnabled().convention(emptyJvmOptions.getDebug());
    }

    @Override
    public Provider<List<String>> getAllJvmArgs() {
        return getJvmArgumentProviders().map(providers -> {
            JvmOptions copy = new JvmOptions(fileCollectionFactory, objectFactory.newInstance(DefaultJavaDebugOptions.class, objectFactory));
            copy.copyFrom(this);
            return copy.getAllJvmArgs();
        });
    }

    @Override
    public List<String> getJvmArgs() {
        return jvmArgs;
    }

    @Override
    public void setJvmArgs(@Nullable List<String> arguments) {
        this.jvmArgs = new ArrayList<>(jvmArgs);
        if (arguments != null) {
            jvmArgs(arguments);
        }
    }

    @Override
    public void setJvmArgs(@Nullable Iterable<?> arguments) {
        this.jvmArgs = new ArrayList<>();
        if (arguments != null) {
            jvmArgs(arguments);
        }
    }

    @Override
    public JavaForkOptions jvmArgs(Iterable<?> arguments) {
        for (Object argument : arguments) {
            // TODO: uncomment once jvmArgs is a ListProperty
            //  if (argument instanceof Provider) {
            //      getJvmArgs().add(((Provider<?>) argument).map(Object::toString));
            //  } else {
            //      getJvmArgs().add(argument.toString());
            //  }
            getJvmArgs().add(argument.toString());
        }
        return this;
    }

    @Override
    public JavaForkOptions jvmArgs(Object... arguments) {
        jvmArgs(Arrays.asList(arguments));
        return this;
    }

    @Override
    public ListProperty<CommandLineArgumentProvider> getJvmArgumentProviders() {
        return jvmArgumentProviders;
    }

    @Override
    public Map<String, Object> getSystemProperties() {
        return systemProperties;
    }

    @Override
    public void setSystemProperties(Map<String, ?> properties) {
        this.systemProperties = new LinkedHashMap<>(properties);
    }

    @Override
    public JavaForkOptions systemProperties(Map<String, ?> properties) {
        properties.forEach(this::systemProperty);
        return this;
    }

    @Override
    public JavaForkOptions systemProperty(String name, Object value) {
        // TODO: uncomment once systemProperties is a MapProperty
        //   if (value instanceof Provider) {
        //      ((MapPropertyInternal<String, Object>) getSystemProperties()).insert(name, (Provider<?>) value);
        //      return this;
        //   }
        if (value == null) {
            getSystemProperties().put(name, NULL);
        } else {
            getSystemProperties().put(name, value);
        }
        return this;
    }

    @Override
    public ConfigurableFileCollection getBootstrapClasspath() {
        return bootstrapClasspath;
    }

    @Override
    public JavaForkOptions bootstrapClasspath(Object... classpath) {
        getBootstrapClasspath().from(classpath);
        return this;
    }

    @Override
    public Property<String> getMinHeapSize() {
        return minHeapSize;
    }

    @Override
    public Property<String> getMaxHeapSize() {
        return maxHeapSize;
    }

    @Override
    public Property<String> getDefaultCharacterEncoding() {
        return defaultCharacterEncoding;
    }

    @Override
    public Property<Boolean> getEnableAssertions() {
        return enableAssertions;
    }

    @Override
    public Property<Boolean> getDebug() {
        return getDebugOptions().getEnabled();
    }

    @Override
    public JavaDebugOptions getDebugOptions() {
        return debugOptions;
    }

    @Override
    public void debugOptions(Action<JavaDebugOptions> action) {
        action.execute(debugOptions);
    }

    @Override
    protected Map<String, ?> getInheritableEnvironment() {
        // Filter out any environment variables that should not be inherited.
        return Jvm.getInheritableEnvironmentVariables(super.getInheritableEnvironment());
    }

    @Override
    public JavaForkOptions copyTo(JavaForkOptions target) {
        super.copyTo(target);
        target.setJvmArgs(getJvmArgs());
        target.setSystemProperties(getSystemProperties());
        target.getMinHeapSize().set(getMinHeapSize());
        target.getMaxHeapSize().set(getMaxHeapSize());
        target.bootstrapClasspath(getBootstrapClasspath());
        target.getEnableAssertions().set(getEnableAssertions());
        JvmOptions.copyDebugOptions(this.getDebugOptions(), target.getDebugOptions());
        target.getJvmArgumentProviders().set(getJvmArgumentProviders());
        return this;
    }

    @Override
    public boolean isCompatibleWith(JavaForkOptions options) {
        if (hasJvmArgumentProviders(this) || hasJvmArgumentProviders(options)) {
            throw new UnsupportedOperationException("Cannot compare options with jvmArgumentProviders.");
        }
        return Objects.equals(getDebug().get(), options.getDebug().get())
            && Objects.equals(getEnableAssertions().get(), options.getEnableAssertions().get())
            && normalized(getExecutable()).equals(normalized(options.getExecutable()))
            && getWorkingDir().equals(options.getWorkingDir())
            && normalized(getDefaultCharacterEncoding().getOrNull()).equals(normalized(options.getDefaultCharacterEncoding().getOrNull()))
            && getHeapSizeMb(getMinHeapSize().getOrNull()) >= getHeapSizeMb(options.getMinHeapSize().getOrNull())
            && getHeapSizeMb(getMaxHeapSize().getOrNull()) >= getHeapSizeMb(options.getMaxHeapSize().getOrNull())
            && normalized(getJvmArgs()).containsAll(normalized(options.getJvmArgs()))
            && containsAll(getSystemProperties(), options.getSystemProperties())
            && containsAll(getEnvironment(), options.getEnvironment())
            && getBootstrapClasspath().getFiles().containsAll(options.getBootstrapClasspath().getFiles());
    }

    @Override
    public void checkDebugConfiguration(Iterable<?> arguments) {
        JvmOptions.checkDebugConfiguration(getDebugOptions(), arguments);
    }

    @Override
    public void setExtraJvmArgs(Iterable<?> arguments) {
        this.extraJvmArgs = arguments;
    }

    @Override
    public Iterable<?> getExtraJvmArgs() {
        return extraJvmArgs;
    }

    private static boolean hasJvmArgumentProviders(JavaForkOptions forkOptions) {
        return forkOptions instanceof DefaultJavaForkOptions
            && hasJvmArgumentProviders((DefaultJavaForkOptions) forkOptions);
    }

    private static boolean hasJvmArgumentProviders(DefaultJavaForkOptions forkOptions) {
        return !isNullOrEmpty(forkOptions.getJvmArgumentProviders().get());
    }

    private static <T> boolean isNullOrEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }
}

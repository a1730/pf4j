/*
 * Copyright (C) 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pf4j;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * A {@link PluginRepository} that delegates to a list of {@link PluginRepository}s.
 * The first applicable {@link PluginRepository} is used to get the plugin paths.
 * If no {@link PluginRepository} is applicable, a {@link RuntimeException} is thrown.
 * The order of the {@link PluginRepository}s is important.
 *
 * @author Decebal Suiu
 * @author Mário Franco
 */
public class CompoundPluginRepository implements PluginRepository {

    private final List<PluginRepository> repositories = new ArrayList<>();

    public CompoundPluginRepository add(PluginRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("null not allowed");
        }

        repositories.add(repository);

        return this;
    }

    /**
     * Add a {@link PluginRepository} only if the {@code condition} is satisfied.
     *
     * @param repository the {@link PluginRepository} to add
     * @param condition the condition to be satisfied
     * @return this {@link CompoundPluginRepository}
     */
    public CompoundPluginRepository add(PluginRepository repository, BooleanSupplier condition) {
        if (condition.getAsBoolean()) {
            return add(repository);
        }

        return this;
    }

    @Override
    public List<Path> getPluginPaths() {
        Set<Path> paths = new LinkedHashSet<>();
        for (PluginRepository repository : repositories) {
            paths.addAll(repository.getPluginPaths());
        }

        return new ArrayList<>(paths);
    }

    @Override
    public boolean deletePluginPath(Path pluginPath) {
        for (PluginRepository repository : repositories) {
            if (repository.deletePluginPath(pluginPath)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the list of {@link PluginRepository}s.
     *
     * @return the list of {@link PluginRepository}s
     */
    public List<PluginRepository> getRepositories() {
        return repositories;
    }

}

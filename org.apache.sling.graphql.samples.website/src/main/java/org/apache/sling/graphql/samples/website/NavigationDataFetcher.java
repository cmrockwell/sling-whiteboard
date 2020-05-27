/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sling.graphql.samples.website;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

class NavigationDataFetcher implements DataFetcher<Object> {

    public static final String NAME = "navigation";
    public static final String CONTENT_ROOT = "/content/articles";

    private final Resource resource;

    static class Section {
        private final String name;
        private final String path;

        Section(Resource r) {
            final ValueMap vm = r.adaptTo(ValueMap.class);
            name = vm == null ? null : vm.get("name", String.class);
            path = r.getPath();
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }
    }

    NavigationDataFetcher(Resource resource) {
        this.resource = resource;
    }

    private Object[] getSections() {
        final List<Section> result = new ArrayList<>();
        final Resource root = resource.getResourceResolver().getResource(CONTENT_ROOT);
        final Iterable<Resource> it = () -> root.getResourceResolver().getChildren(root).iterator();
        StreamSupport.stream(it.spliterator(), false)
            .forEach(child -> result.add(new Section(child)));
        return result.toArray();

    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        final Map<String, Object> result = new HashMap<>();
        result.put("root", CONTENT_ROOT);
        result.put("sections", getSections());
        return result;
    }
}
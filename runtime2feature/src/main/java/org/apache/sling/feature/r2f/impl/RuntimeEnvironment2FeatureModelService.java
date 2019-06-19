/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.sling.feature.r2f.impl;

import static java.nio.file.Files.newBufferedReader;
import static java.util.Objects.requireNonNull;
import static org.apache.sling.feature.io.json.FeatureJSONReader.read;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.sling.feature.ArtifactId;
import org.apache.sling.feature.Feature;
import org.apache.sling.feature.r2f.ConversionRequest;
import org.apache.sling.feature.r2f.RuntimeEnvironment2FeatureModel;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public final class RuntimeEnvironment2FeatureModelService implements RuntimeEnvironment2FeatureModel {

    private static final String SLING_FEATURE_PROPERTY_NAME = "sling.feature";

    @Override
    public Feature getLaunchFeature(BundleContext bundleContext) {
        String launchFeatureLocation = bundleContext.getProperty(SLING_FEATURE_PROPERTY_NAME);

        if (launchFeatureLocation == null) {
            throw new IllegalStateException("Framework property 'sling.feature' is not set, impossible to assemble the launch Feature");
        }

        URI launchFeatureURI = URI.create(launchFeatureLocation);
        Path launchFeaturePath = Paths.get(launchFeatureURI);

        try (BufferedReader reader = newBufferedReader(launchFeaturePath)) {
            return read(reader, launchFeatureLocation);
        } catch (IOException cause) {
            throw new UncheckedIOException(cause);
        }
    }

    @Override
    public Feature getRuntimeFeature(ConversionRequest conversionRequest) {
        ArtifactId resultId = requireNonNull(conversionRequest.getResultId(), "Impossible to create the Feature with a null id");
        BundleContext bundleContext = requireNonNull(conversionRequest.getBundleContext(), "Impossible to create the Feature from a null BundleContext");

        Feature targetFeature = new Feature(resultId);

        // collect all bundles

        Bundle[] bundles = bundleContext.getBundles();
        if (bundles != null) {
            Bundle2ArtifactMapper mapper = new Bundle2ArtifactMapper(targetFeature);

            Stream.of(bundles).map(mapper).forEach(mapper);
        }

        // collect all configurations

        ServiceReference<ConfigurationAdmin> configurationAdminReference = bundleContext.getServiceReference(ConfigurationAdmin.class);
        if (configurationAdminReference != null) {
            ConfigurationAdmin configurationAdmin = bundleContext.getService(configurationAdminReference);
            try {
                Configuration[] configurations = configurationAdmin.listConfigurations(null);
                if (configurations != null) {
                    OSGiConfiguration2FeatureConfigurationMapper mapper = new OSGiConfiguration2FeatureConfigurationMapper(targetFeature);

                    Stream.of(configurations).map(mapper).forEach(mapper);
                }
            } catch (Exception e) {
                // that should not happen
                throw new RuntimeException("Something went wrong while iterating over all available Configurations", e);
            }
        }

        return targetFeature;
    }

}

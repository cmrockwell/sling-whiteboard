/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.metrics.osgi.impl;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.apache.felix.systemready.SystemReady;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.BundleContext;

@RunWith(PaxExam.class)
public class SystemReadySmokeIT extends AbstractIT {

    @Inject
    protected BundleContext bc;

    @Override
    protected Option[] specificOptions() {
        return options(
            mavenBundle("org.apache.felix", "org.apache.felix.systemready", "0.4.2"),
            mavenBundle("org.apache.felix", "org.apache.felix.rootcause", "0.1.0")
        );
    }
    protected void markSystemReady() {
        bc.registerService(SystemReady.class, new SystemReady() {}, null);
    }

}

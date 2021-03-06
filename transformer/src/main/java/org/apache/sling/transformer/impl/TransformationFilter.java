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
package org.apache.sling.transformer.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.transformer.TransformationContext;
import org.apache.sling.transformer.TransformationManager;
import org.apache.sling.transformer.TransformationStep;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * This filter activates the rewriter for the output.
 *
 */
@Component(service = Filter.class, property = { Constants.SERVICE_VENDOR + "=The Apache Software Foundation",
        "sling.filter.scope=request", "sling.filter.scope=error",
        Constants.SERVICE_RANKING + ":Integer=" + Integer.MIN_VALUE })
public class TransformationFilter implements Filter {

    @Reference
    private TransformationManager manager;

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        // nothing to do
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        // nothing to do
    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof SlingHttpServletRequest)) {
            throw new ServletException("Request is not a Apache Sling HTTP request.");
        }

        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;

        List<TransformationStep> steps = manager.getSteps(slingRequest);

        if (!steps.isEmpty()) {
            TransformationContext context = new TransformationContextImpl(slingRequest, slingResponse, steps);
            steps.forEach(transformer -> transformer.before(context));
            response = new TransformationResponse(context);
            chain.doFilter(request, response);
            steps.forEach(transformer -> transformer.after(context));
            response.flushBuffer();
        } else {
            chain.doFilter(request, response);
        }
    }
}

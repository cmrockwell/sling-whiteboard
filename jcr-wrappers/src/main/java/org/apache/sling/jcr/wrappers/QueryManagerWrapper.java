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
package org.apache.sling.jcr.wrappers;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.qom.QueryObjectModelFactory;

public class QueryManagerWrapper extends BaseWrapper<QueryManager> implements QueryManager {

    public QueryManagerWrapper(SessionWrapper<?> sessionWrapper, QueryManager delegate) {
        super(sessionWrapper, delegate);
    }

    @Override
    public Query createQuery(String statement, String language) throws InvalidQueryException, RepositoryException {
        return sessionWrapper.getObjectWrapper().wrap(this.sessionWrapper, delegate.createQuery(statement, language));
    }

    @Override
    public QueryObjectModelFactory getQOMFactory() {
        return new QueryObjectModelFactoryWrapper(this.sessionWrapper, delegate.getQOMFactory());
    }

    @Override
    public Query getQuery(Node node) throws InvalidQueryException, RepositoryException {
        return sessionWrapper.getObjectWrapper().wrap(sessionWrapper, delegate.getQuery(node));
    }

    @Override
    public String[] getSupportedQueryLanguages() throws RepositoryException {
        return delegate.getSupportedQueryLanguages();
    }
}

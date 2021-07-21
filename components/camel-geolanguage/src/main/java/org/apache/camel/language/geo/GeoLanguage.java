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
package org.apache.camel.language.geo;

import org.apache.camel.CamelContext;
import org.apache.camel.Expression;
import org.apache.camel.Predicate;
import org.apache.camel.spi.PropertyConfigurer;
import org.apache.camel.spi.annotations.Language;
import org.apache.camel.support.LanguageSupport;

/**
 * Geospatial language.
 */
@Language("geo")
public class GeoLanguage extends LanguageSupport implements PropertyConfigurer {

    @Override
    public Predicate createPredicate(String expression) {
        expression = loadResource(expression);

        return GeoPredicate.geopath(expression);
    }

    @Override
    public Expression createExpression(String expression) {
        expression = loadResource(expression);

        return GeoExpression.geopath(expression);
    }

    @Override
    public Predicate createPredicate(String expression, Object[] properties) {
        return this.createPredicate(expression);
    }

    @Override
    public Expression createExpression(String expression, Object[] properties) {
        return this.createExpression(expression);
    }

    @Override
    public boolean configure(CamelContext camelContext, Object target, String name, Object value, boolean ignoreCase) {
        if (target != this) {
            throw new IllegalStateException("Can only configure our own instance !");
        }

        //Nothing to configure, yet
        return true;
    }

}

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
import org.apache.camel.CamelContextAware;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 *
 */
public class GeoPredicate implements CamelContextAware, Predicate {
    private static final Logger LOG = LoggerFactory.getLogger(GeoPredicate.class);

    private String text;
    private BinarySpatialOperator operator;
    private CamelContext camelContext;

    /**
     * @param text The GeoPredicate expression
     */
    GeoPredicate(String text) {
        this.text = text;
        String[] words = this.text.split("\\s+");
        if (words.length != 3) {
            throw new RuntimeException("Bad written predicate. It should be 'property verb property'.");
        }
        String geometryA = words[0];
        String operation = words[1];
        String geometryB = words[2];

        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        Expression expressionA = new GeoProperty(geometryA);
        Expression expressionB = new GeoProperty(geometryB);

        switch (operation.toUpperCase(Locale.ROOT)) {
            case "DISJOINT":
                operator = ff.disjoint(expressionA, expressionB);
                break;
            case "CROSSES":
                operator = ff.crosses(expressionA, expressionB);
                break;
            case "CONTAINS":
            default:
                operator = ff.contains(expressionA, expressionB);
                break;
        }
    }

    /**
     * @param text The GeoPath expression
     * @return A new GeoPathBuilder object
     */
    public static GeoPredicate geopath(String text) {
        return new GeoPredicate(text);
    }

    @Override
    public boolean matches(Exchange exchange) {
        return operator.evaluate(exchange);

    }

    @Override
    public void init(CamelContext context) {
        setCamelContext(context);
    }

    @Override
    public void initPredicate(CamelContext context) {
        Predicate.super.initPredicate(context);
    }

    @Override
    public String toString() {
        return "GeoPredicate: '" + this.text + "'";
    }

    @Override
    public CamelContext getCamelContext() {
        return this.camelContext;
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }
}

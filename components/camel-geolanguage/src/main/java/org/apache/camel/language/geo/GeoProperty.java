package org.apache.camel.language.geo;

import org.apache.camel.Exchange;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;

public class GeoProperty implements Expression {

    private String propertyName;

    GeoProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public Object evaluate(Object o) {
        Exchange e = (Exchange) o;
        return e.getProperty(this.propertyName);
    }

    @Override
    public <T> T evaluate(Object o, Class<T> aClass) {
        Exchange e = (Exchange) o;
        return (T) e.getProperty(this.propertyName);
    }

    @Override
    public Object accept(ExpressionVisitor visitor, Object o) {
        return true;
    }
}

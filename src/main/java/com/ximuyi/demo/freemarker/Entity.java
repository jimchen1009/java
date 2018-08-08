package com.ximuyi.demo.freemarker;

import java.util.List;

public class Entity {
    private String packageName;
    private String className;
    private List<Property> properties;

    public Entity(String className, String packageName, List<Property> properties) {
        this.className = className;
        this.packageName = packageName;
        this.properties = properties;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}

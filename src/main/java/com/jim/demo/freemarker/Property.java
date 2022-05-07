package com.jim.demo.freemarker;

public class Property {
    private String javaCls;
    // 属性数据类型
    private String javaType;
    // 属性名称
    private String propertyName;

    public Property(Class<?> cls, String propertyName) {
        this.javaCls = cls.getName();
        this.javaType = cls.getSimpleName();
        this.propertyName = propertyName;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getJavaCls() {
        return javaCls;
    }

    public void setJavaCls(String javaCls) {
        this.javaCls = javaCls;
    }
}

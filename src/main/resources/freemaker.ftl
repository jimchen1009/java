package ${entity.packageName};

<#list entity.properties as property>
import ${property.javaCls};
</#list>

public class ${entity.className} {
<#list entity.properties as property>
    private ${property.javaType} ${property.propertyName};
</#list>

    public ${entity.className}() {
    }

    public ${entity.className}(<#list entity.properties as property>${property.javaType} ${property.propertyName}<#if property_has_next>, </#if></#list>) {
    }

<#list entity.properties as property>
    public ${property.javaType} get${property.propertyName?cap_first}() {
		return ${property.propertyName};
	}

	public void set${property.propertyName?cap_first}(${property.javaType} ${property.propertyName}) {
		this.${property.propertyName} = ${property.propertyName};
	}
    <#if property_has_next>

    </#if>
</#list>
}

/**
 * This class was generated by Smooks EJC (http://www.smooks.org).
 */
package ${class.packageName};

<#list class.imports as importClass>
import ${importClass.name};    
</#list>

public class ${class.className} {

    <#list class.properties as property>
    private ${property};
    </#list>
    <#list class.methods as method>

    public ${method.signature} {
        ${method.body}
    }
    </#list>
}
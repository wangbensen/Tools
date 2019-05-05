package com.vx.tools.mybatis.generator.plugins;

import com.vx.tools.mybatis.generator.elements.*;
import com.vx.tools.mybatis.generator.method.MybatisExtendMethodGenerator;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MybatisExtendPlugin extends PluginAdapter {

    private String lockColumn = "";
    private List<String> additionColumns = new ArrayList<>();
    private List<String> excludeUpdateColumns = new ArrayList<>();

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String lockColumn = getProperties().getProperty("lockColumn");
        if(lockColumn != null && lockColumn.trim().length() >= 0){
            this.lockColumn = lockColumn;
        }
        String additionColumns = getProperties().getProperty("additionColumns");
        if(additionColumns != null && additionColumns.trim().length() >= 0){
            this.additionColumns.addAll(Arrays.asList(StringUtils.split(additionColumns, ",")));
        }
        String excludeUpdateColumns = getProperties().getProperty("excludeUpdateColumns");
        if(excludeUpdateColumns != null && excludeUpdateColumns.trim().length() >= 0){
            this.excludeUpdateColumns.addAll(Arrays.asList(StringUtils.split(excludeUpdateColumns, ",")));
        }
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    private void setAdditionColumnsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable){
        if(additionColumns.size() <= 0){
            return;
        }

        List<Parameter> parameters = method.getParameters();
        //修改原来的参数
        for(Parameter parameter : parameters){
            parameter.addAnnotation("@Param(\"" + parameter.getName() + "\")");
        }
        //添加附加的参数
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            String escapedColumnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            if(additionColumns.contains(escapedColumnName)){
                parameters.add(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), introspectedColumn.getJavaProperty(),
                        "@Param(\""+introspectedColumn.getJavaProperty()+"\")"));
            }
        }
    }


    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        setAdditionColumnsMethodGenerated(method, interfaze, introspectedTable);
        return super.clientDeleteByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if(additionColumns.size() > 0){
            DeleteByPrimaryKeyElementExtendGenerated elementGenerator = new DeleteByPrimaryKeyElementExtendGenerated();
            elementGenerator.setWhereColumns(additionColumns);
            elementGenerator.setContext(context);
            elementGenerator.setIntrospectedTable(introspectedTable);
            elementGenerator.addElements(element);
        }
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }





    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        setAdditionColumnsMethodGenerated(method, interfaze, introspectedTable);
        return super.clientSelectByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if(additionColumns.size() > 0){
            SelectByPrimaryKeyElementExtendGenerated elementGenerator = new SelectByPrimaryKeyElementExtendGenerated();
            elementGenerator.setWhereColumns(additionColumns);
            elementGenerator.setContext(context);
            elementGenerator.setIntrospectedTable(introspectedTable);
            elementGenerator.addElements(element);
        }
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }





    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        setAdditionColumnsMethodGenerated(method, interfaze, introspectedTable);
        return super.clientUpdateByPrimaryKeySelectiveMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if(additionColumns.size() > 0){
            UpdateByPrimaryKeySelectiveElementExtendGenerated elementGenerator = new UpdateByPrimaryKeySelectiveElementExtendGenerated();
            elementGenerator.setWhereColumns(additionColumns);
            elementGenerator.setExcludeUpdateColumns(excludeUpdateColumns);
            elementGenerator.setContext(context);
            elementGenerator.setIntrospectedTable(introspectedTable);
            elementGenerator.addElements(element);
        }
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }






    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        setAdditionColumnsMethodGenerated(method, interfaze, introspectedTable);
        return super.clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if(additionColumns.size() > 0){
            UpdateByPrimaryKeyWithBLOBsElementExtendGenerated elementGenerator = new UpdateByPrimaryKeyWithBLOBsElementExtendGenerated();
            elementGenerator.setWhereColumns(additionColumns);
            elementGenerator.setExcludeUpdateColumns(excludeUpdateColumns);
            elementGenerator.setContext(context);
            elementGenerator.setIntrospectedTable(introspectedTable);
            elementGenerator.addElements(element);
        }
        return super.sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(element, introspectedTable);
    }






    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        setAdditionColumnsMethodGenerated(method, interfaze, introspectedTable);
        return super.clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if(additionColumns.size() > 0){
            UpdateByPrimaryKeyWithoutBLOBsElementExtendGenerated elementGenerator = new UpdateByPrimaryKeyWithoutBLOBsElementExtendGenerated(true);
            elementGenerator.setWhereColumns(additionColumns);
            elementGenerator.setExcludeUpdateColumns(excludeUpdateColumns);
            elementGenerator.setContext(context);
            elementGenerator.setIntrospectedTable(introspectedTable);
            elementGenerator.addElements(element);
        }
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(element, introspectedTable);
    }



    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        MybatisExtendXmlElementGenerator elementGenerator = new MybatisExtendXmlElementGenerator();
        elementGenerator.setWhereColumns(additionColumns);
        elementGenerator.setExcludeUpdateColumns(excludeUpdateColumns);
        elementGenerator.setLockColumn(lockColumn);
        elementGenerator.setContext(context);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.addElements(document.getRootElement());
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        MybatisExtendMethodGenerator methodGenerator = new MybatisExtendMethodGenerator(true);
        methodGenerator.setWhereColumns(additionColumns);
        methodGenerator.setLockColumn(lockColumn);
        methodGenerator.setContext(context);
        methodGenerator.setIntrospectedTable(introspectedTable);
        methodGenerator.addInterfaceElements(interfaze);
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }
}

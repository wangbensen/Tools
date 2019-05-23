package com.vx.tools.mybatis.generator.plugins;

import com.vx.tools.mybatis.generator.elements.*;
import com.vx.tools.mybatis.generator.method.MybatisExtendMethodGenerator;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.GeneratedXmlFile;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MybatisExtendPlugin extends PluginAdapter {
    //xml是否覆盖
    private Boolean xmlMergeable = false;
    //乐观锁字段
    private String lockColumn = "";
    //逻辑删除字段
    private String logicDeleteColumn = "";
    //模糊查询字段
    private List<String> selectLikeColumns = new ArrayList<>();
    //附加字段
    private List<String> additionColumns = new ArrayList<>();
    //排除更新字段
    private List<String> excludeUpdateColumns = new ArrayList<>();

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String lockColumn = getProperties().getProperty("lockColumn");
        if(lockColumn != null && lockColumn.trim().length() >= 0){
            this.lockColumn = lockColumn;
        }
        String logicDeleteColumn = getProperties().getProperty("logicDeleteColumn");
        if(logicDeleteColumn != null && logicDeleteColumn.trim().length() >= 0){
            this.logicDeleteColumn = logicDeleteColumn;
        }
        String additionColumns = getProperties().getProperty("additionColumns");
        if(additionColumns != null && additionColumns.trim().length() >= 0){
            this.additionColumns.addAll(Arrays.asList(StringUtils.split(additionColumns, ",")));
        }
        String excludeUpdateColumns = getProperties().getProperty("excludeUpdateColumns");
        if(excludeUpdateColumns != null && excludeUpdateColumns.trim().length() >= 0){
            this.excludeUpdateColumns.addAll(Arrays.asList(StringUtils.split(excludeUpdateColumns, ",")));
        }
        String selectLikeColumns = getProperties().getProperty("selectLikeColumns");
        if(selectLikeColumns != null && selectLikeColumns.trim().length() >= 0){
            this.selectLikeColumns.addAll(Arrays.asList(StringUtils.split(selectLikeColumns, ",")));
        }
        String xmlMergeable = getProperties().getProperty("xmlMergeable");
        if(xmlMergeable != null && xmlMergeable.trim().length() >= 0){
            this.xmlMergeable = Boolean.getBoolean(xmlMergeable);
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
        DeleteByPrimaryKeyElementExtendGenerated elementGenerator = new DeleteByPrimaryKeyElementExtendGenerated();
        elementGenerator.setWhereColumns(additionColumns);
        elementGenerator.setContext(context);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.addElements(element);
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }





    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        setAdditionColumnsMethodGenerated(method, interfaze, introspectedTable);
        return super.clientSelectByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        SelectByPrimaryKeyElementExtendGenerated elementGenerator = new SelectByPrimaryKeyElementExtendGenerated();
        elementGenerator.setWhereColumns(additionColumns);
        elementGenerator.setLogicDeleteColumn(logicDeleteColumn);
        elementGenerator.setContext(context);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.addElements(element);
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }





    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        setAdditionColumnsMethodGenerated(method, interfaze, introspectedTable);
        return super.clientUpdateByPrimaryKeySelectiveMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        UpdateByPrimaryKeySelectiveElementExtendGenerated elementGenerator = new UpdateByPrimaryKeySelectiveElementExtendGenerated();
        elementGenerator.setWhereColumns(additionColumns);
        elementGenerator.setExcludeUpdateColumns(excludeUpdateColumns);
        elementGenerator.setContext(context);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.addElements(element);
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }






    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        setAdditionColumnsMethodGenerated(method, interfaze, introspectedTable);
        return super.clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        UpdateByPrimaryKeyWithBLOBsElementExtendGenerated elementGenerator = new UpdateByPrimaryKeyWithBLOBsElementExtendGenerated();
        elementGenerator.setWhereColumns(additionColumns);
        elementGenerator.setExcludeUpdateColumns(excludeUpdateColumns);
        elementGenerator.setContext(context);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.addElements(element);
        return super.sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(element, introspectedTable);
    }






    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        setAdditionColumnsMethodGenerated(method, interfaze, introspectedTable);
        return super.clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        UpdateByPrimaryKeyWithoutBLOBsElementExtendGenerated elementGenerator = new UpdateByPrimaryKeyWithoutBLOBsElementExtendGenerated(true);
        elementGenerator.setWhereColumns(additionColumns);
        elementGenerator.setExcludeUpdateColumns(excludeUpdateColumns);
        elementGenerator.setContext(context);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.addElements(element);
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(element, introspectedTable);
    }



    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        MybatisExtendXmlElementGenerator elementGenerator = new MybatisExtendXmlElementGenerator();
        elementGenerator.setWhereColumns(additionColumns);
        elementGenerator.setExcludeUpdateColumns(excludeUpdateColumns);
        elementGenerator.setSelectLikeColumns(selectLikeColumns);
        elementGenerator.setLockColumn(lockColumn);
        elementGenerator.setLogicDeleteColumn(logicDeleteColumn);
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
        methodGenerator.setLogicDeleteColumn(logicDeleteColumn);
        methodGenerator.setSelectLikeColumns(selectLikeColumns);
        methodGenerator.setContext(context);
        methodGenerator.setIntrospectedTable(introspectedTable);
        methodGenerator.addInterfaceElements(interfaze);
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        if(!xmlMergeable){
            try{
                Field field = sqlMap.getClass().getDeclaredField("isMergeable");
                field.setAccessible(true);
                field.setBoolean(sqlMap,false);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return super.sqlMapGenerated(sqlMap, introspectedTable);
    }
}

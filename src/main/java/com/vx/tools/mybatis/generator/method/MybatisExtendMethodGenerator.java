package com.vx.tools.mybatis.generator.method;

import com.vx.tools.mybatis.generator.enums.ExtendInternalAttribute;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MybatisExtendMethodGenerator extends AbstractJavaMapperMethodGenerator {

    private boolean isSimple;
    private String lockColumn = "";
    private String logicDeleteColumn = "";
    private List<String> selectLikeColumns = new ArrayList<>();
    private List<String> whereColumns = new ArrayList<>();

    public void setLogicDeleteColumn(String logicDeleteColumn) {
        this.logicDeleteColumn = logicDeleteColumn;
    }

    public void setWhereColumns(List<String> whereColumns) {
        this.whereColumns = whereColumns;
    }

    public void setLockColumn(String lockColumn) {
        this.lockColumn = lockColumn;
    }

    public void setSelectLikeColumns(List<String> selectLikeColumns) {
        this.selectLikeColumns = selectLikeColumns;
    }

    public MybatisExtendMethodGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        addSelectOneElement(interfaze);
        addSelectListElement(interfaze);
        addSelectPageElement(interfaze);
        if(lockColumn != null && lockColumn.trim().length() > 0){
            addUpdateByPrimaryKeySelectiveLockElement(interfaze);
        }
    }

    private void setAdditionColumnsMethodGenerated(Method method){
        if(whereColumns.size() <= 0){
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
            if(whereColumns.contains(escapedColumnName)){
                parameters.add(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), introspectedColumn.getJavaProperty(),
                        "@Param(\""+introspectedColumn.getJavaProperty()+"\")"));
            }
        }
    }

    private void addSelectOneElement(Interface interfaze){
        //先创建import对象
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        //添加Lsit的包
        importedTypes.add(FullyQualifiedJavaType.getNewListInstance());
        //创建方法对象
        Method method = new Method();
        //设置该方法为public
        method.setVisibility(JavaVisibility.PUBLIC);

        //设置返回值类型
        FullyQualifiedJavaType returnType = introspectedTable.getRules().calculateAllFieldsClass();
        method.setReturnType(returnType);
        importedTypes.add(returnType);
        method.setName(ExtendInternalAttribute.ATTR_SELECT_ONE.getName());

        //设置参数类型是对象
        FullyQualifiedJavaType parameterType;
        if (isSimple) {
            parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        } else {
            parameterType = introspectedTable.getRules().calculateAllFieldsClass();
        }
        //import参数类型对象
        importedTypes.add(parameterType);
        //为方法添加参数，变量名称record
        method.addParameter(new Parameter(parameterType, "record"));

        //添加附加的参数
        setAdditionColumnsMethodGenerated(method);

        context.getCommentGenerator().addGeneralMethodComment(method,introspectedTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

    private void addSelectListElement(Interface interfaze){
        //先创建import对象
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        //添加Lsit的包
        importedTypes.add(FullyQualifiedJavaType.getNewListInstance());
        //创建方法对象
        Method method = new Method();
        //设置该方法为public
        method.setVisibility(JavaVisibility.PUBLIC);
        //设置返回类型是List
        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        FullyQualifiedJavaType listType;
        //设置List的类型是实体类的对象
        listType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        importedTypes.add(listType);
        //返回类型对象设置为List
        returnType.addTypeArgument(listType);
        //方法对象设置返回类型对象
        method.setReturnType(returnType);
        //设置方法名称为我们在IntrospectedTable类中初始化的 “selectByObject”
        method.setName(ExtendInternalAttribute.ATTR_SELECT_LIST.getName());

        //设置参数类型是对象
        FullyQualifiedJavaType parameterType;
        if (isSimple) {
            parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        } else {
            parameterType = introspectedTable.getRules().calculateAllFieldsClass();
        }
        //import参数类型对象
        importedTypes.add(parameterType);
        method.addParameter(new Parameter(parameterType, "record"));

        //添加附加的参数
        setAdditionColumnsMethodGenerated(method);

        context.getCommentGenerator().addGeneralMethodComment(method,introspectedTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

    private void addSelectPageElement(Interface interfaze){
        //先创建import对象
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        //添加Lsit的包
        importedTypes.add(FullyQualifiedJavaType.getNewListInstance());
        //创建方法对象
        Method method = new Method();
        //设置该方法为public
        method.setVisibility(JavaVisibility.PUBLIC);
        //设置返回类型是List
        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        FullyQualifiedJavaType listType;
        //设置List的类型是实体类的对象
        listType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        importedTypes.add(listType);
        //返回类型对象设置为List
        returnType.addTypeArgument(listType);
        //方法对象设置返回类型对象
        method.setReturnType(returnType);
        //设置方法名称为我们在IntrospectedTable类中初始化的
        method.setName(ExtendInternalAttribute.ATTR_SELECT_PAGE.getName());

        //设置参数类型是对象
        FullyQualifiedJavaType parameterType;
        if (isSimple) {
            parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        } else {
            parameterType = introspectedTable.getRules().calculateAllFieldsClass();
        }
        //import参数类型对象
        importedTypes.add(parameterType);
        method.addParameter(new Parameter(parameterType, "record", "@Param(\"record\")"));

        FullyQualifiedJavaType pageable= new FullyQualifiedJavaType("org.springframework.data.domain.Pageable");
        method.addParameter(new Parameter(pageable, "pageable","@Param(\"pageable\")"));
        importedTypes.add(pageable);
        importedTypes.add(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));

        //添加附加的参数
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            String escapedColumnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            if(whereColumns.contains(escapedColumnName)){
                method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), introspectedColumn.getJavaProperty(),
                        "@Param(\""+introspectedColumn.getJavaProperty()+"\")"));
            }
        }

        context.getCommentGenerator().addGeneralMethodComment(method,introspectedTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

    private void addUpdateByPrimaryKeySelectiveLockElement(Interface interfaze){
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(ExtendInternalAttribute.UPDATE_BY_PRIMARY_KEY_SELECTIVE_LOCK.getName());

        FullyQualifiedJavaType parameterType;
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            parameterType = new FullyQualifiedJavaType(introspectedTable.getRecordWithBLOBsType());
        } else {
            parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        }
        importedTypes.add(parameterType);
        method.addParameter(new Parameter(parameterType, "record"));

        //添加附加的参数
        setAdditionColumnsMethodGenerated(method);

        context.getCommentGenerator().addGeneralMethodComment(method,introspectedTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }
}

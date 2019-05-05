package com.vx.tools.mybatis.generator.elements;

import com.vx.tools.mybatis.generator.enums.ExtendInternalAttribute;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.List;

public class MybatisExtendXmlElementGenerator extends AbstractXmlElementGenerator {

    private String lockColumn = "";
    private List<String> whereColumns = new ArrayList<>();
    private List<String> excludeUpdateColumns = new ArrayList<>();

    public void setWhereColumns(List<String> whereColumns) {
        this.whereColumns = whereColumns;
    }

    public void setExcludeUpdateColumns(List<String> excludeUpdateColumns) {
        this.excludeUpdateColumns = excludeUpdateColumns;
    }

    public void setLockColumn(String lockColumn) {
        this.lockColumn = lockColumn;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        addSelectOneElement(parentElement);
        addSelectListElement(parentElement);
        addSelectPageElement(parentElement);
        if(lockColumn != null && lockColumn.trim().length() > 0){
            addUpdateByPrimaryKeySelectiveLockElement(parentElement, lockColumn);
        }
    }

    private void addSelectOneElement(XmlElement parentElement) {
        //先创建一个select标签
        XmlElement answer = new XmlElement("select");
        //设置该select标签的id，正式第二篇里在枚举中设置的值
        answer.addAttribute(new Attribute("id", ExtendInternalAttribute.ATTR_SELECT_ONE.getName()));
        if(whereColumns.size() <= 0){
            //添加parameterType
            String parameterType;
            if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
                parameterType = introspectedTable.getRecordWithBLOBsType();
            } else {
                parameterType = introspectedTable.getBaseRecordType();
            }
            answer.addAttribute(new Attribute("parameterType", parameterType));
        }

        //设置resultMap为BaseResultMap
        answer.addAttribute(new Attribute("resultMap",introspectedTable.getBaseResultMapId()));

        context.getCommentGenerator().addComment(answer);
        //接下去是拼接我们的sql
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        if (sb.length() > 0) {
            answer.addElement((new TextElement(sb.toString())));
        }
        answer.addElement(getBaseColumnListElement());
        sb.setLength(0);
        sb.append("from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        //在这里添加where条件
        XmlElement selectTrimElement = new XmlElement("trim");
        selectTrimElement.addAttribute(new Attribute("prefix", "where"));
        selectTrimElement.addAttribute(new Attribute("prefixOverrides", "and"));

        answer.addElement(selectTrimElement);
        //循环所有的列
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            XmlElement selectNotNullElement = new XmlElement("if");
            sb.setLength(0);
            sb.append((whereColumns.size() > 0 ? "record." : "") + introspectedColumn.getJavaProperty());
            sb.append(" != null ");
            selectNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            sb.setLength(0);
            sb.append(" and ");
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, whereColumns.size() > 0 ? "record." : ""));
            selectNotNullElement.addElement(new TextElement(sb.toString()));
            selectTrimElement.addElement(selectNotNullElement);
        }

        //添加where附加条件
        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns())) {
            String escapedColumnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            if(whereColumns.contains(escapedColumnName)){
                answer.addElement(new TextElement("and " + escapedColumnName + " = " + MyBatis3FormattingUtilities.getParameterClause(introspectedColumn)));
            }
        }

        String orderByClause = introspectedTable.getTableConfigurationProperty(PropertyRegistry.TABLE_SELECT_ALL_ORDER_BY_CLAUSE);
        boolean hasOrderBy = StringUtility.stringHasValue(orderByClause);
        if (hasOrderBy) {
            sb.setLength(0);
            sb.append("order by ");
            sb.append(orderByClause);
            answer.addElement(new TextElement(sb.toString()));
        }

        //添加一条限制
        answer.addElement(new TextElement("limit 1"));
        parentElement.addElement(answer);
    }

    private void addSelectPageElement(XmlElement parentElement) {
        //先创建一个select标签
        XmlElement answer = new XmlElement("select");
        //设置该select标签的id，正式第二篇里在枚举中设置的值
        answer.addAttribute(new Attribute("id", ExtendInternalAttribute.ATTR_SELECT_PAGE.getName()));
        //设置resultMap为BaseResultMap
        answer.addAttribute(new Attribute("resultMap",introspectedTable.getBaseResultMapId()));

        context.getCommentGenerator().addComment(answer);
        //接下去是拼接我们的sql
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        if (sb.length() > 0) {
            answer.addElement((new TextElement(sb.toString())));
        }
        answer.addElement(getBaseColumnListElement());
        sb.setLength(0);
        sb.append("from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        //在这里添加where条件
        XmlElement selectTrimElement = new XmlElement("trim");
        selectTrimElement.addAttribute(new Attribute("prefix", "where"));
        selectTrimElement.addAttribute(new Attribute("prefixOverrides", "and"));

        answer.addElement(selectTrimElement);

        //循环所有的列
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            XmlElement selectNotNullElement = new XmlElement("if");
            sb.setLength(0);
            sb.append("record."+introspectedColumn.getJavaProperty());
            sb.append(" != null ");
            selectNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            sb.setLength(0);
            sb.append(" and ");
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append((MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "record.")));
            selectNotNullElement.addElement(new TextElement(sb.toString()));
            selectTrimElement.addElement(selectNotNullElement);
        }

        //添加where附加条件
        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns())) {
            String escapedColumnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            if(whereColumns.contains(escapedColumnName)){
                answer.addElement(new TextElement("and " + escapedColumnName + " = " + MyBatis3FormattingUtilities.getParameterClause(introspectedColumn)));
            }
        }

        String orderByClause = introspectedTable.getTableConfigurationProperty(PropertyRegistry.TABLE_SELECT_ALL_ORDER_BY_CLAUSE);
        boolean hasOrderBy = StringUtility.stringHasValue(orderByClause);
        if (hasOrderBy) {
            sb.setLength(0);
            sb.append("order by ");
            sb.append(orderByClause);
            answer.addElement(new TextElement(sb.toString()));
        }

        //添加分页查询的orderby参数 by henghui.zhang
        sb.setLength(0);
        XmlElement selectNotNullElement = new XmlElement("if");
        selectNotNullElement.addAttribute(new Attribute("test", "pageable != null and pageable.sort != null"));
        selectNotNullElement.addElement(new TextElement("order by"));

        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "pageable.sort"));
        foreachElement.addAttribute(new Attribute("item", "order"));
        foreachElement.addAttribute(new Attribute("separator", ","));
        foreachElement.addElement(new TextElement("${order.property} ${order.direction}"));

        selectNotNullElement.addElement(foreachElement);
        answer.addElement(selectNotNullElement);

        parentElement.addElement(answer);
    }

    private void addSelectListElement(XmlElement parentElement){
        //先创建一个select标签
        XmlElement answer = new XmlElement("select");
        //设置该select标签的id，正式第二篇里在枚举中设置的值
        answer.addAttribute(new Attribute("id", ExtendInternalAttribute.ATTR_SELECT_LIST.getName()));
        if(whereColumns.size() <= 0){
            //添加parameterType
            String parameterType;
            if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
                parameterType = introspectedTable.getRecordWithBLOBsType();
            } else {
                parameterType = introspectedTable.getBaseRecordType();
            }
            answer.addAttribute(new Attribute("parameterType", parameterType));
        }
        //设置resultMap为BaseResultMap
        answer.addAttribute(new Attribute("resultMap",introspectedTable.getBaseResultMapId()));

        context.getCommentGenerator().addComment(answer);
        //接下去是拼接我们的sql
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        if (sb.length() > 0) {
            answer.addElement((new TextElement(sb.toString())));
        }
        answer.addElement(getBaseColumnListElement());
        sb.setLength(0);
        sb.append("from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        //在这里添加where条件
        XmlElement selectTrimElement = new XmlElement("trim");
        selectTrimElement.addAttribute(new Attribute("prefix", "where"));
        selectTrimElement.addAttribute(new Attribute("prefixOverrides", "and"));

        answer.addElement(selectTrimElement);
        //循环所有的列
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            XmlElement selectNotNullElement = new XmlElement("if");
            sb.setLength(0);
            sb.append((whereColumns.size() > 0 ? "record." : "") + introspectedColumn.getJavaProperty());
            sb.append(" != null ");
            selectNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            sb.setLength(0);
            sb.append(" and ");
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, whereColumns.size() > 0 ? "record." : ""));
            selectNotNullElement.addElement(new TextElement(sb.toString()));
            selectTrimElement.addElement(selectNotNullElement);
        }

        //添加where附加条件
        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns())) {
            String escapedColumnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            if(whereColumns.contains(escapedColumnName)){
                answer.addElement(new TextElement("and " + escapedColumnName + " = " + MyBatis3FormattingUtilities.getParameterClause(introspectedColumn)));
            }
        }

        String orderByClause = introspectedTable.getTableConfigurationProperty(PropertyRegistry.TABLE_SELECT_ALL_ORDER_BY_CLAUSE);
        boolean hasOrderBy = StringUtility.stringHasValue(orderByClause);
        if (hasOrderBy) {
            sb.setLength(0);
            sb.append("order by ");
            sb.append(orderByClause);
            answer.addElement(new TextElement(sb.toString()));
        }

        parentElement.addElement(answer);
    }


    private void addUpdateByPrimaryKeySelectiveLockElement(XmlElement parentElement, String lockColumn){
        XmlElement answer = new XmlElement("update");
        answer.addAttribute(new Attribute("id", ExtendInternalAttribute.UPDATE_BY_PRIMARY_KEY_SELECTIVE_LOCK.getName()));
        if(whereColumns.size() <= 0){
            String parameterType;
            if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
                parameterType = introspectedTable.getRecordWithBLOBsType();
            } else {
                parameterType = introspectedTable.getBaseRecordType();
            }
            answer.addAttribute(new Attribute("parameterType", parameterType));
        }
        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("set");
        answer.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns())) {
            String escapedColumnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            if(excludeUpdateColumns.contains(escapedColumnName)){
                continue;
            }
            XmlElement isNotNullElement = new XmlElement("if");
            sb.setLength(0);
            sb.append((whereColumns.size() > 0 ? "record." : "") + introspectedColumn.getJavaProperty());
            sb.append(" != null");
            isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(escapedColumnName);
            sb.append(" = ");
            if(escapedColumnName.equalsIgnoreCase(lockColumn)){
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, whereColumns.size() > 0 ? "record." : "")+" + 1");
            }else{
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, whereColumns.size() > 0 ? "record." : ""));
            }
            sb.append(',');

            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            String escapedColumnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            if(excludeUpdateColumns.contains(escapedColumnName)){
                continue;
            }
            sb.setLength(0);
            if (and) {
                sb.append("  and ");
            } else {
                sb.append("where ");
                and = true;
            }

            sb.append(escapedColumnName);
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, whereColumns.size() > 0 ? "record." : ""));
            answer.addElement(new TextElement(sb.toString()));
        }

        //添加where附加条件
        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns())) {
            String escapedColumnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            if(escapedColumnName.equalsIgnoreCase(lockColumn)){
                answer.addElement(new TextElement("and " + escapedColumnName + " = " + MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, whereColumns.size() > 0 ? "record." : "")));
            }
            if(whereColumns.contains(escapedColumnName)){
                answer.addElement(new TextElement("and " + escapedColumnName + " = " + MyBatis3FormattingUtilities.getParameterClause(introspectedColumn)));
            }
        }

        parentElement.addElement(answer);
    }
}

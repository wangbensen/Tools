package com.vx.tools.mybatis.generator.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.util.ArrayList;
import java.util.List;

public class UpdateByPrimaryKeySelectiveElementExtendGenerated extends AbstractXmlElementGenerator {

    private List<String> whereColumns = new ArrayList<>();
    private List<String> excludeUpdateColumns = new ArrayList<>();

    public void setWhereColumns(List<String> whereColumns) {
        this.whereColumns = whereColumns;
    }

    public void setExcludeUpdateColumns(List<String> excludeUpdateColumns) {
        this.excludeUpdateColumns = excludeUpdateColumns;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        //重新生成attribute
        List<Attribute> attributes = parentElement.getAttributes();
        attributes.clear();
        attributes.add(new Attribute("id", introspectedTable.getUpdateByPrimaryKeySelectiveStatementId()));
        //重新生成element
        List<Element> elements = parentElement.getElements();
        elements.clear();
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        elements.add(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("set");
        elements.add(dynamicElement);

        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable .getNonPrimaryKeyColumns())) {
            String escapedColumnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            if(excludeUpdateColumns.contains(escapedColumnName)){
                continue;
            }
            XmlElement isNotNullElement = new XmlElement("if");
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null");
            isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(escapedColumnName);
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "record."));
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
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "record."));
            elements.add(new TextElement(sb.toString()));
        }

        //添加where附加条件
        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns())) {
            String escapedColumnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            if(whereColumns.contains(escapedColumnName)){
                elements.add(new TextElement("and " + escapedColumnName + " = " + MyBatis3FormattingUtilities.getParameterClause(introspectedColumn)));
            }
        }
    }
}

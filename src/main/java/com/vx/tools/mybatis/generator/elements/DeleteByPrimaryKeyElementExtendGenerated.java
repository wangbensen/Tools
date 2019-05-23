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

public class DeleteByPrimaryKeyElementExtendGenerated extends AbstractXmlElementGenerator {

    private List<String> whereColumns = new ArrayList<>();

    public void setWhereColumns(List<String> whereColumns) {
        this.whereColumns = whereColumns;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        //重新生成attribute
        List<Attribute> attributes = parentElement.getAttributes();
        attributes.clear();
        attributes.add(new Attribute("id", introspectedTable.getDeleteByPrimaryKeyStatementId()));
        //重新生成element
        List<Element> elements = parentElement.getElements();
        elements.clear();
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        elements.add(new TextElement(sb.toString()));

        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("  and ");
            } else {
                sb.append("where ");
                and = true;
            }
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            elements.add(new TextElement(sb.toString()));
        }

        //添加where附加条件
        if(whereColumns.size() > 0){
            for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns())) {
                String escapedColumnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
                if(whereColumns.contains(escapedColumnName)){
                    elements.add(new TextElement("and " + escapedColumnName + " = " + MyBatis3FormattingUtilities.getParameterClause(introspectedColumn)));
                }
            }
        }
    }
}

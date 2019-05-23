package com.vx.tools.mybatis.generator.elements;

import org.apache.commons.lang3.StringUtils;
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

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class SelectByPrimaryKeyElementExtendGenerated extends AbstractXmlElementGenerator {

    private List<String> whereColumns = new ArrayList<>();
    private String logicDeleteColumn = "";

    public void setWhereColumns(List<String> whereColumns) {
        this.whereColumns = whereColumns;
    }

    public void setLogicDeleteColumn(String logicDeleteColumn) {
        this.logicDeleteColumn = logicDeleteColumn;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        //重新生成attribute
        List<Attribute> attributes = parentElement.getAttributes();
        attributes.clear();
        attributes.add(new Attribute("id", introspectedTable.getSelectByPrimaryKeyStatementId()));
        if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
            attributes.add(new Attribute("resultMap",introspectedTable.getResultMapWithBLOBsId()));
        } else {
            attributes.add(new Attribute("resultMap",introspectedTable.getBaseResultMapId()));
        }
        //重新生成element
        List<Element> elements = parentElement.getElements();
        elements.clear();
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        if (stringHasValue(introspectedTable.getSelectByPrimaryKeyQueryId())) {
            sb.append('\'');
            sb.append(introspectedTable.getSelectByPrimaryKeyQueryId());
            sb.append("' as QUERYID,");
        }
        elements.add(new TextElement(sb.toString()));
        elements.add(getBaseColumnListElement());
        if (introspectedTable.hasBLOBColumns()) {
            elements.add(new TextElement(","));
            elements.add(getBlobColumnListElement());
        }

        sb.setLength(0);
        sb.append("from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
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

            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            elements.add(new TextElement(sb.toString()));
        }

        //逻辑删除字段
        if(StringUtils.isNotBlank(logicDeleteColumn)){
            elements.add(new TextElement(" and " + logicDeleteColumn + " = 1 "));
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

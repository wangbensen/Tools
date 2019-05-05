package com.vx.tools.mybatis.generator.enums;

public enum ExtendInternalAttribute {
    /**查询one方法*/
    ATTR_SELECT_ONE("selectOne"),

    /**查询list方法*/
    ATTR_SELECT_LIST("selectList"),

    /**查询Page方法*/
    ATTR_SELECT_PAGE("selectPage"),

    /**查询Page方法*/
    UPDATE_BY_PRIMARY_KEY_SELECTIVE_LOCK("updateByPrimaryKeySelectiveLock");

    private String name;

    ExtendInternalAttribute(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

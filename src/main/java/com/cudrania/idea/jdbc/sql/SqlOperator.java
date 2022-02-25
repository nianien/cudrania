package com.cudrania.idea.jdbc.sql;

/**
 * 枚举器,Sql语法中的匹配运算符
 *
 * @author skyfalling
 */
public enum SqlOperator {

    /**
     * "="
     */
    Equal("? = ?"),
    /**
     * "!="
     */
    NotEqual("? != ?"),
    /**
     * ">"
     */
    Greater("? > ?"),
    /**
     * "<"
     */
    Less("? < ?"),
    /**
     * ">="
     */
    GreaterEqual("? >= ?"),
    /**
     * "<="
     */
    LessEqual("? <= ?"),
    /**
     * "like"
     */
    Like("? like ?"),
    /**
     * "not like"
     */
    NotLike("? not like ?"),
    /**
     * "in"
     */
    In("? in ?"),
    /**
     * "not in"
     */
    NotIn("? not in ?"),
    /**
     * "is null"
     */
    IsNull("? is null"),
    /**
     * "is null"
     */
    IsNotNull("? is not null"),
    /**
     * "between and"
     */
    Between("? between ? and ?"),
    /**
     * "not between and"
     */
    NotBetween("? not between ? and ?"),
    /**
     * "and"
     */
    And("and"),
    /**
     * "or"
     */
    Or("or"),
    /**
     * "exist"
     */
    Exist("exist"),
    /**
     * "not exist"
     */
    NotExist("not exist"),
    /**
     * "not"
     */
    Not("not"),
    /**
     * ","
     */
    Comma(",");

    /**
     * 构造方法,私有访问权限
     *
     * @param value
     */
    SqlOperator(String value) {
        this.value = value;
    }

    /**
     * 枚举实例对应的值
     */
    private String value;

    /**
     * 操作符对应的Sql模板
     *
     * @return
     */
    @Override
    public String toString() {
        return this.value;
    }

    /**
     * 根据变量赋值生成SQL
     *
     * @param name
     * @return
     */
    public String toSQL(String name) {
        return this.value.replaceFirst("\\?", name);
    }

}

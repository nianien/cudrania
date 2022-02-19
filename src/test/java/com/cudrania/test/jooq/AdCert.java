package com.cudrania.test.jooq;

import com.cudrania.side.jooq.Match;
import com.cudrania.side.jooq.Operator;
import lombok.Data;

import java.util.Date;

@Data
public class AdCert {

    /**
     * 资质ID
     */
    private Integer id;
    /**
     * 资质类型
     */
    private Integer certCategory;
    /**
     * 资质链接
     */
    private String url;
    /**
     * 过期时间
     */
    @Match(op = Operator.BETWEEN)
    private Date[] expireDate;


    private Date createDate;


    /**
     * 审核状态
     */
    private Integer status;

    /**
     * 审核详情
     */
    @Match(op = Operator.LIKE)
    private String statusDetail;
}
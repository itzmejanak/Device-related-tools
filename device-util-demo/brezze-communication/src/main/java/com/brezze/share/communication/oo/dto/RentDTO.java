package com.brezze.share.communication.oo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "RentDTO", description = "租借成功返回的对象")
public class RentDTO implements Serializable {

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号", dataType = "int", allowableValues = "")
    private String orderNo;
    /**
     * 仓位号
     */
    @ApiModelProperty(value = "仓位号", dataType = "int", allowableValues = "")
    private Integer pos;
    /**
     * 充电宝编号
     */
    @ApiModelProperty(value = "充电宝编号", dataType = "string", allowableValues = "")
    private String pbNo;


}

package com.brezze.share.communication.oo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "SysConfigPreAuthAmountDTO", description = "预授权金额配置")
public class SysConfigPreAuthAmountDTO {

    /**
     * value
     */
    @ApiModelProperty(value = "value", allowableValues = "")
    private String value;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", allowableValues = "")
    private String comment;
    /**
     * key
     */
    @ApiModelProperty(value = "key", allowableValues = "")
    private String keyName;
    /**
     * 货币编码
     */
    @ApiModelProperty(value = "货币编码", allowableValues = "")
    private String currency;

}

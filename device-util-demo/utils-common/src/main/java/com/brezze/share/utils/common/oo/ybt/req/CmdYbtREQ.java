package com.brezze.share.utils.common.oo.ybt.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@ApiModel(value = "CmdYbtREQ", description = "下发指令请求对象")
@Accessors(chain = true)
@Data
public class CmdYbtREQ implements Serializable {
    /**
     * 机柜编号（通讯编号）
     */
    @ApiModelProperty(value = "机柜编号（通讯编号）", dataType = "string", required = true, allowableValues = "")
    private String cabinetNo;
    /**
     * 充电宝编号
     */
    @ApiModelProperty(value = "充电宝编号", dataType = "string", allowableValues = "")
    private String pbNo;
    /**
     * 仓位号
     */
    @ApiModelProperty(value = "仓位号", dataType = "Integer", allowableValues = "")
    private Integer pos;
    /**
     * 串口号
     */
    @ApiModelProperty(value = "串口号", dataType = "Integer", allowableValues = "")
    private Integer io;
    /**
     * 自定义域名
     */
    @ApiModelProperty(value = "自定义域名", dataType = "String", allowableValues = "")
    private String domain;
    /**
     * WIFI账号
     */
    @ApiModelProperty(value = "WIFI账号", dataType = "String", allowableValues = "")
    private String username;
    /**
     * WIFI密码
     */
    @ApiModelProperty(value = "WIFI密码", dataType = "String", allowableValues = "")
    private String password;
    /**
     * 数据
     */
    @ApiModelProperty(value = "数据", dataType = "String", allowableValues = "")
    private String data;

    public Integer getIo() {
        if (io == null) {
            return 0;
        }
        return io;
    }
}

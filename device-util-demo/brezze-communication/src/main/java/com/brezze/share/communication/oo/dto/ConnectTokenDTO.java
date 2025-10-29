package com.brezze.share.communication.oo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ConnectTokenDTO", description = "stripe终端连接秘钥")
public class ConnectTokenDTO {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "id", dataType = "int", allowableValues = "")
    private String secret;
}

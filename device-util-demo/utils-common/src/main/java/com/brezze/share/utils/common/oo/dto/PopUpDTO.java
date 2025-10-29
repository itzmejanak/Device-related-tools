package com.brezze.share.utils.common.oo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "PopUpDTO", description = "设备弹出间隔")
public class PopUpDTO implements Serializable {

    /**
     * 最后弹出时间
     */
    @ApiModelProperty(value = "最后弹出时间", dataType = "Long", example = "")
    private LocalDateTime lastTime;
}

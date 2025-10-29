package com.brezze.share.communication.oo.dto;

import com.brezze.share.utils.common.oo.ybt.message.ReceiveUpload;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author :xiefujian
 * @description : 设备详情-倍斯特
 * @date :2022-12-22 18:45:08
 **/
@ApiModel(value = "DeviceDetailDTO", description = "设备详情-倍斯特")
@Data
public class DeviceDetailDTO {
    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号(IMEI)", required = false, example = "")
    private String deviceName;
    /**
     * 设备详情数据
     */
    @ApiModelProperty(value = "设备详情数据", required = false, example = "")
    private ReceiveUpload data;
}

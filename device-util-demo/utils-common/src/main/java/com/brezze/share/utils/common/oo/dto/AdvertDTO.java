package com.brezze.share.utils.common.oo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author :xiefujian
 * @description :机柜广告
 * @date :2021-12-07 11:38:41
 **/
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "AdvertDTO", description = "机柜广告")
public class AdvertDTO {

    /**
     * 广告标题
     */
    @ApiModelProperty(value = "广告标题", dataType = "String", allowableValues = "")
    private String title;

    /**
     * 文件类型：1-图片 2-视频
     */
    @ApiModelProperty(value = "文件类型：1-图片 2-视频", dataType = "Integer", allowableValues = "1,2")
    private Integer fileType;

    /**
     * 20口以下的机器广告地址（备注：图片类型jpg/png,  视频类型：mp4）
     */
    @ApiModelProperty(value = "20口以下的机器广告地址（备注：图片类型jpg/png,  视频类型：mp4）", dataType = "String", allowableValues = "")
    private String url1;

    /**
     * 20口以上的机器广告地址（备注：图片类型jpg/png,  视频类型：mp4）
     */
    @ApiModelProperty(value = "20口以上的机器广告地址（备注：图片类型jpg/png,  视频类型：mp4）", dataType = "String", allowableValues = "")
    private String url2;

    /**
     * 预留字段，默认空字符串
     */
    @ApiModelProperty(value = "预留字段，默认空字符串", dataType = "String", allowableValues = "")
    private String url3;

    /**
     * 默认空字符串 或者 不返回
     */
    @ApiModelProperty(value = "默认空字符串 或者 不返回", dataType = "String", allowableValues = "")
    private String forward;

    /**
     * 图片轮播时间，默认5
     */
    @ApiModelProperty(value = "图片轮播时间，默认5", dataType = "Integer", allowableValues = "")
    private Integer playTime;

    /**
     * 音量， 0 - 100，默认0
     */
    @ApiModelProperty(value = "音量， 0 - 100，默认0", dataType = "Integer", allowableValues = "")
    private Integer weight;
    /**
     * 屏幕亮度 0 - 255，默认0
     */
    @ApiModelProperty(value = "屏幕亮度 0 - 255，默认0", dataType = "String", allowableValues = "")
    private Integer screenBrightness;
    /**
     * 默认null
     */
    @ApiModelProperty(value = "默认null", dataType = "String", allowableValues = "")
    private Integer guuid;
}

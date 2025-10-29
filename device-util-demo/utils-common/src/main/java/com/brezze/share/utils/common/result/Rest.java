package com.brezze.share.utils.common.result;

import com.brezze.share.utils.common.enums.hint.Hint;
import com.brezze.share.utils.common.exception.BrezException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "Rest", description = "通用返回对象")

public class Rest<T> implements Serializable {
    /**
     * 返回码
     */
    @ApiModelProperty(value = "返回码", dataType = "string", required = true, allowableValues = "0, 1, ...")
    private String code;

    /**
     * 返回数据 T
     */
    @ApiModelProperty(value = "返回数据", dataType = "Object", required = true, allowableValues = "null, {}, []")
    private T data;

    /**
     * 返回消息
     */
    @ApiModelProperty(value = "返回消息", dataType = "string", required = true, allowableValues = "Successful, Failure")
    private String message;

    private Rest() {
    }

    public boolean isSuccess() {
        return Hint.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 返回成功对象
     * @return
     */
    public static Rest success() {

        return new Rest()
                .setCode(Hint.SUCCESS.getCode())
                .setMessage(Hint.SUCCESS.getEnDescription());
    }

    /**
     * 返回成功对象
     * @param data
     * @param <T>
     * @return
     */
    public static<T> Rest<T> success(T data) {

        return new Rest()
                .setCode(Hint.SUCCESS.getCode())
                .setMessage(Hint.SUCCESS.getEnDescription())
                .setData(data);
    }

    /**
     * 返回失败对象
     * @return
     */
    public static Rest failure() {

        return new Rest()
                .setCode(Hint.FAILURE.getCode())
                .setMessage(Hint.FAILURE.getEnDescription());
    }

    /**
     * 返回失败对象
     * @param data
     * @param <T>
     * @return
     */
    public static<T> Rest<T> failure(T data) {

        return new Rest()
                .setCode(Hint.FAILURE.getCode())
                .setMessage(Hint.FAILURE.getEnDescription())
                .setData(data);
    }

    /**
     * 返回指定错误码对象
     * @param hint
     * @param <T>
     * @return
     */
    public static<T> Rest<T> failure(Hint hint) {

        return new Rest()
                .setCode(hint.getCode())
                .setMessage(hint.getEnDescription());
    }

    /**
     * 返回指定错误码对象
     * @param hint
     * @param appendDesc 追加描述
     * @return
     */
    public static<T> Rest<T> failureDesc(Hint hint, String appendDesc) {

        return new Rest()
                .setCode(hint.getCode())
                .setMessage(hint.getEnDescription() + appendDesc);
    }

    /**
     * 返回指定错误码对象
     * @param hint
     * @param data 数据
     * @return
     */
    public static<T> Rest<T> failureData(Hint hint, T data) {

        return new Rest()
                .setCode(hint.getCode())
                .setMessage(hint.getEnDescription())
                .setData(data);
    }

    /**
     * 返回指定错误码对象
     * @param hint
     * @param data
     * @param <T>
     * @return
     */
    public static<T> Rest<T> failure(Hint hint, T data) {

        return new Rest()
                .setCode(hint.getCode())
                .setMessage(hint.getEnDescription())
                .setData(data);
    }

    /**
     * 返回失败对象
     * @param e
     * @param <T>
     * @return
     */
    public static<T> Rest<T> failure(BrezException e) {

        return new Rest()
                .setCode(e.getCode())
                .setMessage(e.getMsg());
    }
}

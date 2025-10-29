package com.brezze.share.utils.common.exception;

import com.brezze.share.utils.common.enums.hint.HintEnum;
import com.brezze.share.utils.common.result.Rest;
import lombok.Data;

@Data
public class BrezException extends RuntimeException {

    private String code;
    private String msg;
    private Object[] params;

    public BrezException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BrezException(HintEnum hint) {
        this.code = hint.getCode();
        this.msg = hint.getEnDescription();
    }

    public BrezException(Rest rest) {
        this.code = rest.getCode();
        this.msg = rest.getMessage();
    }

    public BrezException(HintEnum hint, Object... params) {
        this.code = hint.getCode();
        this.msg = hint.getEnDescription();
        this.params = params;
    }

    public BrezException(String code, String msg, Object... params) {
        this.code = code;
        this.msg = msg;
        this.params = params;
    }
}

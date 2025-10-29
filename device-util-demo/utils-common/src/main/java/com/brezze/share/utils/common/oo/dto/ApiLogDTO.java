package com.brezze.share.utils.common.oo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ApiLogDTO implements Serializable {
    private String uri;
    private String method;
    private String headers;
    private String queryString;
    private String body;
    private String response;
    private String ip;
    private long timestamp;
    private String beginDate;
    private String endDate;
    private String elapsedTime;

    public String format() {
        return
                "\n/************************************************************************************\n" +
                        "method: " + this.method + " uri: " + this.uri + " ip: [" + this.ip + "]\n" +
                        "headers: " + this.headers + "\n" +
                        "queryString: " + this.queryString + "\n" +
                        "body: " + this.body + "\n" +
                        "response: " + this.response + "\n" +
                        "elapsed time: [" + this.elapsedTime + "] range time: [" + this.beginDate + " - " + this.endDate + "]\n" +
                        "************************************************************************************/\n";
    }
}

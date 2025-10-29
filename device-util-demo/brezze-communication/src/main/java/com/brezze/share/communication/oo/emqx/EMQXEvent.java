package com.brezze.share.communication.oo.emqx;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class EMQXEvent {

    private String username;
    private Long timestamp;
    private String sockname;
    private String reason;
    private Integer protoVer;
    private String protoName;
    private String peername;
    private String node;
    private Metadata metadata;
    private String event;
    private Long disconnectedAt;
    private DisconnProps disconnProps;
    private String clientid;

    @NoArgsConstructor
    @Data
    public static class Metadata {
        private String ruleId;
    }

    @NoArgsConstructor
    @Data
    public static class DisconnProps {
        private UserProperty userProperty;

        @NoArgsConstructor
        @Data
        public static class UserProperty {
        }
    }
}

package com.brezze.share.communication.oo.emqx;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class EMQXMessage {

    private String username;
    private String topic;
    private Long timestamp;
    private Integer qos;
    private Long publishReceivedAt;
    private PubProps pubProps;
    private String peerhost;
    private String payload;
    private String node;
    private Metadata metadata;
    private String id;
    private String fromUsername;
    private String fromClientid;
    private Flags flags;
    private String event;
    private String clientid;

    @NoArgsConstructor
    @Data
    public static class PubProps {
        private UserProperty userProperty;

        @NoArgsConstructor
        @Data
        public static class UserProperty {
        }
    }

    @NoArgsConstructor
    @Data
    public static class Metadata {
        private String ruleId;
    }

    @NoArgsConstructor
    @Data
    public static class Flags {
        private Boolean retain;
        private Boolean dup;
    }
}

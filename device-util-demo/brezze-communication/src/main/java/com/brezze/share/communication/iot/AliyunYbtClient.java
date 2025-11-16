package com.brezze.share.communication.iot;

import com.brezze.share.communication.config.YbtConfig;
import com.brezze.share.communication.iot.listener.IotYbtListener;
import com.brezze.share.utils.common.string.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.JmsConnectionListener;
import org.apache.qpid.jms.message.JmsInboundMessageDispatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.net.URI;
import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AliyunYbtClient implements CommandLineRunner {

    @Autowired
    private YbtConfig ybtConfig;

    //业务处理异步线程池，线程池参数可以根据您的业务特点调整，或者您也可以用其他异步方式处理接收到的消息。
    private final static ExecutorService executorService = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue(50000));

    @Override
    public void run(String... args) throws Exception {
        log.info("是否开启AliyunIotYbtClient-[{}]", ybtConfig.isEnable());
        if (!ybtConfig.isEnable()) {
            return;
        }
        log.info("开始连接iot mqtt[YBT]====================================================================================");
        //参数说明，请参见：AMQP客户端接入说明。
        long timeStamp = System.currentTimeMillis();
        //签名方法：支持hmacmd5，hmacsha1和hmacsha256
        String signMethod = "hmacsha1";
        //控制台服务端订阅中消费组状态页客户端ID一栏将显示clientId参数。
        //建议使用机器UUID、MAC地址、IP等唯一标识等作为clientId。便于您区分识别不同的客户端。
        String clientId = UUID.randomUUID().toString().replaceAll("-", "");
        //UserName组装方法，请参见上一篇文档：AMQP客户端接入说明。
        String userName;
        if (StringUtil.isNotEmpty(ybtConfig.getIotInstanceId())) {
            userName = clientId + "|iotInstanceId=" + ybtConfig.getIotInstanceId()
                    + ",authMode=aksign"
                    + ",signMethod=" + signMethod
                    + ",timestamp=" + timeStamp
                    + ",authId=" + ybtConfig.getAccessKey()
                    + ",consumerGroupId=" + ybtConfig.getConsumerGroupId()
                    + "|";
        } else {
            userName = clientId + "|authMode=aksign"
                    + ",signMethod=" + signMethod
                    + ",timestamp=" + timeStamp
                    + ",authId=" + ybtConfig.getAccessKey()
                    + ",consumerGroupId=" + ybtConfig.getConsumerGroupId()
                    + "|";
        }
        //password组装方法，请参见上一篇文档：AMQP客户端接入说明。
        String signContent = "authId=" + ybtConfig.getAccessKey() + "&timestamp=" + timeStamp;
        String password = doSign(signContent, ybtConfig.getAccessSecret(), signMethod);
        //按照qpid-jms的规范，组装连接URL。
        String connectionUrl = String.format(
                "failover:(amqps://%s.iot-amqp.%s.aliyuncs.com:5671?amqp.idleTimeout=80000)?failover.reconnectDelay=30", ybtConfig.getUid(), ybtConfig.getRegionId()
        );

        Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.put("connectionfactory.SBCF", connectionUrl);
        hashtable.put("queue.QUEUE", "default");
        hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        Context context = new InitialContext(hashtable);
        ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");
        Destination queue = (Destination) context.lookup("QUEUE");
        // Create Connection
        Connection connection = cf.createConnection(userName, password);
        ((JmsConnection) connection).addConnectionListener(myJmsConnectionListener);
        // Create Session
        // Session.CLIENT_ACKNOWLEDGE: 收到消息后，需要手动调用message.acknowledge()
        // Session.AUTO_ACKNOWLEDGE: SDK自动ACK（推荐）
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        connection.start();
        // Create Receiver Link
        MessageConsumer consumer = session.createConsumer(queue);
        consumer.setMessageListener(new IotYbtListener());
        log.info("成功连接iot mqtt[YBT]====================================================================================");
    }

    private JmsConnectionListener myJmsConnectionListener = new JmsConnectionListener() {
        /**
         * 连接成功建立。
         */
        @Override
        public void onConnectionEstablished(URI remoteURI) {
            log.info("onConnectionEstablished, remoteUri:{}", remoteURI);
        }

        /**
         * 尝试过最大重试次数之后，最终连接失败。
         */
        @Override
        public void onConnectionFailure(Throwable error) {
            log.error("onConnectionFailure, {}", error.getMessage());
        }

        /**
         * 连接中断。
         */
        @Override
        public void onConnectionInterrupted(URI remoteURI) {
            log.info("onConnectionInterrupted, remoteUri:{}", remoteURI);
        }

        /**
         * 连接中断后又自动重连上。
         */
        @Override
        public void onConnectionRestored(URI remoteURI) {
            log.info("onConnectionRestored, remoteUri:{}", remoteURI);
        }

        @Override
        public void onInboundMessage(JmsInboundMessageDispatch envelope) {
        }

        @Override
        public void onSessionClosed(Session session, Throwable cause) {
        }

        @Override
        public void onConsumerClosed(MessageConsumer consumer, Throwable cause) {
        }

        @Override
        public void onProducerClosed(MessageProducer producer, Throwable cause) {
        }
    };

    /**
     * password签名计算方法，请参见上一篇文档：AMQP客户端接入说明。
     */
    private String doSign(String toSignString, String secret, String signMethod) throws Exception {
        SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), signMethod);
        Mac mac = Mac.getInstance(signMethod);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(toSignString.getBytes());
        return Base64.encodeBase64String(rawHmac);
    }
}

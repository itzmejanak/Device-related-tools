package com.brezze;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * BrezzeCommnunicationApplication
 *
 * @Author penf
 * @Description 通讯服务
 * @Date 2020/03/31 04:41
 */
@SpringBootApplication
@MapperScan("com.brezze.share.communication.cabinet.mapper")
@EnableTransactionManagement
public class BrezzeCommnunicationApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrezzeCommnunicationApplication.class, args);
    }

}

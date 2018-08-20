package com.xianglesong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class SparkDemoApplication {

    public static final Logger logger = LoggerFactory.getLogger(SparkDemoApplication.class);

    public static void main(String[] args) {
        logger.info("boot start up.");

        ApplicationContext context = SpringApplication.run(SparkDemoApplication.class, args);

        logger.info("boot end.");
    }
}

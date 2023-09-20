package com.akun.generator.config.dynamicDS;

import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.processor.DsSessionProcessor;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidDynamicDataSourceConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author: Akun
 * @Date: 2019/4/23
 * @Version 1.0
 * @Description:
 */
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@AutoConfigureBefore(DynamicDataSourceAutoConfiguration.class)
@Import(DruidDynamicDataSourceConfiguration.class)
@AutoConfigureOrder(3)
public class DynamicConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DsProcessor dsProcessor() {
        CustomDsHeaderProcessor headerProcessor = new CustomDsHeaderProcessor();
        DsSessionProcessor sessionProcessor = new DsSessionProcessor();
        CustomDsSpelExpressionProcessor spelExpressionProcessor = new CustomDsSpelExpressionProcessor();
        headerProcessor.setNextProcessor(sessionProcessor);
        sessionProcessor.setNextProcessor(spelExpressionProcessor);
        return headerProcessor;
    }

}

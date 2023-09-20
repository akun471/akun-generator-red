package com.akun.generator.config.dynamicDS;

import com.akun.generator.entity.DataSourceEntity;
import com.akun.generator.service.DataSourceEntityService;
import com.baomidou.dynamic.datasource.DynamicDataSourceCreator;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Akun
 * @Date: 2019/2/28
 * @Version 1.0
 * @Description:
 */
@Slf4j
@Component
@AutoConfigureAfter(DynamicDataSourceAutoConfiguration.class)
public class CustomDynamicDataSourceProvider implements InitializingBean {
    @Autowired
    private DynamicRoutingDataSource dataSource;

    private DataSourceEntityService dataSourcePropertyService;

    private DynamicDataSourceCreator dynamicDataSourceCreator;

    @Autowired
    public CustomDynamicDataSourceProvider(DataSourceEntityService dataSourcePropertyService, DynamicDataSourceCreator dynamicDataSourceCreator) {
        this.dataSourcePropertyService = dataSourcePropertyService;
        this.dynamicDataSourceCreator = dynamicDataSourceCreator;
    }

    public Map<String, DataSource> loadDataSources() {
        List<DataSourceEntity> list = dataSourcePropertyService.selectValidList();
        Map<String, DataSource> dataSourceMap = new HashMap<>(list.size());
        for (DataSourceEntity dse : list) {
            String pollName = dse.getPollName();
            DataSourceProperty dataSourceProperty = new DataSourceProperty();
            dataSourceProperty.setPollName(pollName);
            dataSourceProperty.setUsername(dse.getUsername());
            dataSourceProperty.setPassword(dse.getPassword());
            dataSourceProperty.setUrl(dse.getUrl());
            dataSourceProperty.setDriverClassName(dse.getDriverClassName());

            dataSourceMap.put(pollName, dynamicDataSourceCreator.createDruidDataSource(dataSourceProperty));
        }
        return dataSourceMap;
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, DataSource> dataSources = loadDataSources();
        if (dataSources != null) {
            log.info("从JDBC共加载 {} 个数据源", dataSources.size());
            //添加并分组数据源
            for (Map.Entry<String, DataSource> dsItem : dataSources.entrySet()) {
                dataSource.addDataSource(dsItem.getKey(), dsItem.getValue());
            }
        }
    }
}

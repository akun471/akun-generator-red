package com.akun.generator.service.impl;

import com.akun.generator.dao.DataSourceEntityDao;
import com.akun.generator.entity.DataSourceEntity;
import com.akun.generator.service.DataSourceEntityService;
import com.baomidou.dynamic.datasource.DynamicDataSourceCreator;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * dataSource 配置表 服务实现类
 * </p>
 *
 * @author ywpt
 * @since 2019-04-24 16:47:13
 */
@Service
@DS("primary")
public class DataSourceEntityServiceImpl extends ServiceImpl<DataSourceEntityDao, DataSourceEntity> implements DataSourceEntityService {

    @Autowired
    private DynamicRoutingDataSource dataSource;

    @Autowired
    private DynamicDataSourceCreator dynamicDataSourceCreator;

    @Override
    public List<DataSourceEntity> selectValidList() {
        return super.selectList(Condition.create().eq("state", 1));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertOrUpdate(DataSourceEntity entity) {
        updateDs(entity);
        return super.insertOrUpdate(entity);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteById(Serializable id) {
        dataSource.removeDataSource(super.selectById(id).getPollName());
        return super.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(DataSourceEntity entity) {
        if (entity.getState() == 1) {
            updateDs(entity);
        } else {
            dataSource.removeDataSource(entity.getPollName());
        }
        return super.updateById(entity);
    }

    private void updateDs(DataSourceEntity entity) {
        dataSource.removeDataSource(entity.getPollName());
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setPollName(entity.getPollName());
        dataSourceProperty.setUsername(entity.getUsername());
        dataSourceProperty.setPassword(entity.getPassword());
        dataSourceProperty.setUrl(entity.getUrl());
        dataSourceProperty.setDriverClassName(entity.getDriverClassName());
        dataSource.addDataSource(entity.getPollName(), dynamicDataSourceCreator.createDruidDataSource(dataSourceProperty));
    }
}

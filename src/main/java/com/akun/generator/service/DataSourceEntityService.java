package com.akun.generator.service;

import com.akun.generator.entity.DataSourceEntity;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * dataSource 配置表 服务类
 * </p>
 *
 * @author ywpt
 * @since 2019-04-24 16:47:13
 */
public interface DataSourceEntityService extends IService<DataSourceEntity> {


    List<DataSourceEntity> selectValidList();
}

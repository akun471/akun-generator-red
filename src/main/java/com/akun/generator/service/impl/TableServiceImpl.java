package com.akun.generator.service.impl;

import com.akun.generator.dao.TableDao;
import com.akun.generator.entity.Table;
import com.akun.generator.service.TableService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门管理 服务实现类
 * </p>
 *
 * @author Akun
 * @since 2019-06-19 10:22:20
 */
@Service
@DS("#header.dataSource")
public class TableServiceImpl extends ServiceImpl<TableDao, Table> implements TableService {

}

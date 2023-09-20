package com.akun.generator.service;


import com.akun.generator.config.GeneratorParam;
import com.akun.generator.dao.GeneratorMapper;
import com.akun.generator.entity.DataSourceEntity;
import com.akun.generator.utils.GeneratorModelUtils;
import com.akun.generator.utils.GeneratorUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.mapper.Condition;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器
 *
 * @author  Akun
 */
@Service
@DS("#header.dataSource")
public class GeneratorService {
    @Autowired
    private GeneratorMapper generatorMapper;

    @Autowired
    private DataSourceEntityService dataSourceEntityService;

    public List<Map<String, Object>> queryList(Map<String, Object> map) {
        int offset = Integer.parseInt(map.get("offset").toString());
        int limit = Integer.parseInt(map.get("limit").toString());
        map.put("offset", offset);
        map.put("limit", limit);
        return generatorMapper.queryList(map);
    }

    public int queryTotal(Map<String, Object> map) {
        return generatorMapper.queryTotal(map);
    }

    public Map<String, String> queryTable(String tableName) {
        return generatorMapper.queryTable(tableName);
    }

    public List<Map<String, String>> queryColumns(String tableName) {
        return generatorMapper.queryColumns(tableName);
    }

    public byte[] generatorCode(GeneratorParam param) {
        String[] tableNames = new String[]{};
        tableNames = JSON.parseArray(param.getTables()).toArray(tableNames);
        //配置信息

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        List<String> tableNameList = new ArrayList<>();
        for (String tableName : tableNames) {
            //查询表信息
            Map<String, String> table = queryTable(tableName);
            //查询列信息
            List<Map<String, String>> columns = queryColumns(tableName);
            //生成代码
            GeneratorUtils.generatorCode(table, columns, zip, param);

            tableNameList.add(GeneratorUtils.toLowerCaseFirstOne(GeneratorUtils.tableToJava(tableName, param.getTablePrefix())));
        }


        DataSourceEntity dataSourceEntity = dataSourceEntityService.selectOne(Condition.create().eq("poll_name", DynamicDataSourceContextHolder.peek()));
        //生成代码
        GeneratorModelUtils.generatorCode(zip, param, dataSourceEntity,tableNameList);

        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }
}

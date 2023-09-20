package com.akun.generator.controller;

import cn.gitv.op.common.msg.BaseResponse;
import cn.gitv.op.common.msg.ListRestResponse;
import cn.gitv.op.common.util.ConvertUtils;
import cn.gitv.op.controller.BaseController;
import com.akun.generator.entity.DataSourceEntity;
import com.akun.generator.service.DataSourceEntityService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.mapper.Condition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 部门管理 前端控制器
 * </p>
 *
 * @author Akun
 * @since 2019-06-19 10:22:20
 */

@RestController
@RequestMapping("dataSourceEntity")
@Slf4j
@DS("primary")
public class DataSourceEntityController extends BaseController<DataSourceEntity> {

    private final DataSourceEntityService dataSourceEntityService;

    @Autowired
    public DataSourceEntityController(DataSourceEntityService dataSourceEntityService) {
        super.service = dataSourceEntityService;
        this.dataSourceEntityService = dataSourceEntityService;
    }


    @RequestMapping(value = "add", method = RequestMethod.POST)
    public BaseResponse addDataSourceEntity(@RequestBody DataSourceEntity dataSourceEntity) {
        return addModel(dataSourceEntity);
    }


    @RequestMapping(value = "addOrUpdate", method = RequestMethod.POST)
    public BaseResponse addOrUpdate(@RequestBody DataSourceEntity dataSourceEntity) {
        return addOrUpdateModel(dataSourceEntity);
    }


    @RequestMapping(value = "addOrUpdateAllColumn", method = RequestMethod.POST)
    public BaseResponse insertOrUpdateAllColumn(@RequestBody DataSourceEntity dataSourceEntity) {
        return insertOrUpdateAllColumnModel(dataSourceEntity);
    }


    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public BaseResponse editDataSourceEntity(@RequestBody DataSourceEntity dataSourceEntity) {
        return editModel(dataSourceEntity);
    }

    @RequestMapping(value = "modifyState", method = RequestMethod.POST)
    public BaseResponse modifyDataSourceEntityState(@RequestBody JSONObject jsonObject) {
        return modifyState(jsonObject);
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public BaseResponse deleteDataSourceEntity(@PathVariable("id") Integer id) {
        return deleteModel(id);
    }


    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public BaseResponse deleteDataSourceEntity(String idAry) {
        return deleteModel(idAry);
    }


    @RequestMapping(value = "getall", method = RequestMethod.GET)
    public ListRestResponse getAllDataSourceEntity() {
        return getAllModel();
    }

    @RequestMapping(value = "getlist", method = RequestMethod.GET)
    public ListRestResponse getListDataSourceEntity() {
        ListRestResponse listRestResponse = new ListRestResponse<>();
        listRestResponse.setResult(dataSourceEntityService.selectValidList());
        listRestResponse.setMessage("所有信息");
        return listRestResponse;
    }


    @RequestMapping(value = "getpage", method = RequestMethod.POST)
    public ListRestResponse getPageDataSourceEntity(@RequestBody JSONObject jsonObject) {
        return getPage(jsonObject);
    }


    @Override
    public void setConditionInfo(Condition condition, Map.Entry<String, Object> entry, String key) {
        if ("poll_name".equals(ConvertUtils.humpToLine(key))) {
            String value = (String) entry.getValue();
            if (StringUtils.isNotBlank(value)) {
                String[] values = value.split(",");
                for (int i = 0; i < values.length; i++) {
                    if (i == 0) {
                        condition.andNew().like("poll_name", values[i]);
                    } else {
                        condition.or().like("poll_name", values[i]);
                    }
                }
            }
        } else if ("url".equals(ConvertUtils.humpToLine(key))) {
            String value = (String) entry.getValue();
            if (StringUtils.isNotBlank(value)) {
                String[] values = value.split(",");
                for (int i = 0; i < values.length; i++) {
                    if (i == 0) {
                        condition.andNew().like("url", values[i]);
                    } else {
                        condition.or().like("url", values[i]);
                    }
                }
            }
        } else {
            super.setConditionInfo(condition, entry, key);
        }
    }

    @RequestMapping(value = "getModelById/{id}", method = RequestMethod.GET)
    public BaseResponse getModelById(@PathVariable("id") Integer id) {
        return new BaseResponse(200, "查询成功", dataSourceEntityService.selectById(id));
    }


}

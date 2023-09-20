package com.akun.generator.controller;

import cn.gitv.op.common.msg.ListRestResponse;
import cn.gitv.op.common.util.ConvertUtils;
import cn.gitv.op.controller.BaseController;
import com.akun.generator.config.GeneratorParam;
import com.akun.generator.entity.Table;
import com.akun.generator.service.GeneratorService;
import com.akun.generator.service.TableService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.mapper.Condition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
@RequestMapping("table")
@Slf4j
@DS("#header.dataSource")
public class TableController extends BaseController<Table> {

    private final TableService tableService;
    private final GeneratorService generatorService;

    @Autowired
    public TableController(TableService tableService, GeneratorService generatorService) {
        this.generatorService = generatorService;
        super.service = tableService;
        this.tableService = tableService;
    }

    @RequestMapping(value = "getpage", method = RequestMethod.POST)
    public ListRestResponse getPageTable(@RequestBody JSONObject jsonObject) {
        return getPage(jsonObject);
    }

    @Override
    public Condition getCondition(JSONObject jsonObject) {
        Condition condition = Condition.create();
        condition.where("table_schema = (select database())");
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            setConditionOrder(condition, entry, key);
        }
        return condition;
    }

    @Override
    public void setConditionInfo(Condition condition, Map.Entry<String, Object> entry, String key) {
        if ("table_name".equals(ConvertUtils.humpToLine(key))) {
            String value = (String) entry.getValue();
            if (StringUtils.isNotBlank(value)) {
                String[] values = value.split(",");
                for (int i = 0; i < values.length; i++) {
                    if (i == 0) {
                        condition.andNew().like("table_name", values[i]);
                    } else {
                        condition.or().like("table_name", values[i]);
                    }
                }
            }
        } else {
            super.setConditionInfo(condition, entry, key);
        }
    }

    /**
     * 生成代码
     */
    @RequestMapping("/code")
    public void code(GeneratorParam param, HttpServletResponse response) throws IOException {

        byte[] data = generatorService.generatorCode(param);

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=" + param.getModelName() + ".zip");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IOUtils.write(data, response.getOutputStream());
    }
}

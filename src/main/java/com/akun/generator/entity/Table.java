package com.akun.generator.entity;


import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 部门管理
 *
 * @author Akun
 * @email wukunkun@reddatetech.com
 * @date 2019-06-19 10:22:20
 */
@Data
@TableName("information_schema.tables")
public class Table extends Model<Table> {
    private static final long serialVersionUID = 1L;

    @TableField("table_name")
    private String tableName;

    @TableField("table_comment")
    private String tableComment;

    @TableField("engine")
    private String engine;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @Override
    protected Serializable pkVal() {
        return this.tableName;
    }
}
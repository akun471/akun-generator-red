package com.akun.generator.entity;


import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * dataSource 配置表
 *
 * @author ywpt
 * @email ywpt@cnbn.cn
 * @date 2019-04-24 16:47:13
 */
@Data
@TableName("data_source_entity")
public class DataSourceEntity extends Model<DataSourceEntity> {
	private static final long serialVersionUID=1L;
		    //
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
	
		    //连接池名称,映射成DataSource时,分组规则取第一个_前的名称分为一组,详情查看框架源码,或者问akun;
    @TableField("poll_name")
    private String pollName;
	
		    //连接池类型
    @TableField("type")
    private String type;
	
		    //JDBC driver
    @TableField("driver_class_name")
    private String driverClassName;
	
		    //JDBC url地址
    @TableField("url")
    private String url;
	
		    //JDBC 用户名
    @TableField("username")
    private String username;
	
		    //JDBC 密码
    @TableField("password")
    private String password;
	
		    //关联的其他配置
    @TableField("config_id")
    private Integer configId;
	
		    //1:有效; 0:无效; -1:删除不在使用;
    @TableField("state")
    private Integer state;
	
		    //
    @TableField("create_time")
    private Date createTime;
	
		    //
    @TableField("update_time")
    private Date updateTime;
	
		    //详情描述
    @TableField("desc")
    private String desc;

    @TableField(exist = false)
    private DruidConfig druid = new DruidConfig();

		@Override
	protected Serializable pkVal() {
        return this.id;
	}
}
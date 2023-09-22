package com.akun.generator.config;

import lombok.Data;

/**
 * @Author: Akun
 * @Date: 2019/6/19
 * @Version 1.0
 * @Description:
 */
@Data
public class GeneratorParam {

    private String tables;
    private String modelName = "example";
    private String packageName = "com.akun";
    private Integer port = 8888;
    private Integer isLogic = 1;
    private String tablePrefix = "t_";
    private String tableFieldPrefix;
    private String author = "Akun";
    private String email = "wukunkun@reddatetech.com";

}

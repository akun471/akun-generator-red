package com.akun.generator.service;


import com.akun.generator.entity.Menu;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author WWMXD
 * @since 2018-01-02 16:21:35
 */
public interface MenuService extends IService<Menu> {
    public int deleteAll();
}

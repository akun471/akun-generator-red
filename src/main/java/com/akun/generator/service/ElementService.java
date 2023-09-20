package com.akun.generator.service;


import com.akun.generator.entity.Element;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author WWMXD
 * @since 2018-01-02 16:21:36
 */
public interface ElementService extends  IService<Element> {
    public int deleteAll();
}

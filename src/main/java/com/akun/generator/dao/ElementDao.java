package com.akun.generator.dao;

import com.akun.generator.entity.Element;
import com.baomidou.mybatisplus.mapper.BaseMapper;

public interface ElementDao extends BaseMapper<Element> {
    public int deleteAll();

}
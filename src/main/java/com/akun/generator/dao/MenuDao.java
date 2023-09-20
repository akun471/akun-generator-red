package com.akun.generator.dao;

import com.akun.generator.entity.Menu;
import com.baomidou.mybatisplus.mapper.BaseMapper;

public interface MenuDao extends BaseMapper<Menu> {

    public int deleteall();
}
package com.akun.generator.service.impl;


import com.akun.generator.dao.MenuDao;
import com.akun.generator.entity.Menu;
import com.akun.generator.service.MenuService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author WWMXD
 * @since 2018-01-02 16:21:35
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuDao, Menu> implements MenuService {
	@Autowired
    private MenuDao menuDao;

    @Override
    public int deleteAll() {
        return menuDao.deleteall();
    }
}

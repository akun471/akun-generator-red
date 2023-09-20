package com.akun.generator.service.impl;


import com.akun.generator.dao.ElementDao;
import com.akun.generator.entity.Element;
import com.akun.generator.service.ElementService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author WWMXD
 * @since 2018-01-02 16:21:36
 */
@Service
public class ElementServiceImpl extends ServiceImpl<ElementDao, Element> implements ElementService {
    @Autowired
    private ElementDao elementDao;
    @Override
    public int deleteAll() {
        return elementDao.deleteAll();
    }
}

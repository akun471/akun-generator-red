package com.akun.generator.config.dynamicDS;

import com.baomidou.dynamic.datasource.processor.DsProcessor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Akun
 * @Date: 2019/4/23
 * @Version 1.0
 * @Description:
 */
public class CustomDsHeaderProcessor extends DsProcessor {
    /**
     * header开头
     */
    private static final String HEADER_PREFIX = "#header";

    @Override
    public boolean matches(String key) {
        return key.startsWith(HEADER_PREFIX);
    }

    @Override
    public String doDetermineDatasource(MethodInvocation invocation, String key) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //统一转成小写
        if (StringUtils.isNotBlank(request.getHeader(key.substring(8)))) {
            return StringUtils.lowerCase(request.getHeader(key.substring(8)));
        } else {
            return StringUtils.lowerCase(request.getParameter(key.substring(8)));
        }
    }
}

package com.akun.generator.config.dynamicDS;

import com.baomidou.dynamic.datasource.processor.DsProcessor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;

/**
 * @Author: Akun
 * @Date: 2019/4/23
 * @Version 1.0
 * @Description:
 */
public class CustomDsSpelExpressionProcessor extends DsProcessor {

    /**
     * 参数发现器
     */
    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    /**
     * Express语法解析器
     */
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    /**
     * header开头
     */
    private static final String HEADER_PREFIX = "#header";
    /**
     * session开头
     */
    private static final String SESSION_PREFIX = "#session";

    /**
     * @Author Akun
     * @Description
     * @Date 11:00 2019/4/24
     * @Param [key]
     * @return boolean
     **/
    @Override
    public boolean matches(String key) {
        return !key.startsWith(SESSION_PREFIX) && !key.startsWith(HEADER_PREFIX);
    }

    @Override
    public String doDetermineDatasource(MethodInvocation invocation, String key) {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        if (arguments.length <= 0) {
            return null;
        }
        EvaluationContext context = new MethodBasedEvaluationContext(null, method, arguments, NAME_DISCOVERER);
        Expression expression = PARSER.parseExpression(key);
        return PARSER.parseExpression(key).getValue(context).toString();
    }
}
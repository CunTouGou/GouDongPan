package com.hgz.file.config.security.filter;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * URL过滤器（第二个过滤器）：
 * 1. 这个类是分析得出 用户访问的 url 需要哪些权限
 * 2. 核心的方法是第一个
 * 3. 第三个方法返回true表示支持支持这种方式即可
 */
@Component
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    /**
     * 在用户发出请求时，根据请求的url查出该url需要哪些权限才能访问，并将所需权限给SecurityConfig
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        // 允许匿名用户
        return SecurityConfig.createList("ROLE_ANONYMOUS");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}

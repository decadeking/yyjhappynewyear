package com.campus.lostfound.config;

import cn.hutool.core.util.StrUtil;
import com.campus.lostfound.common.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private CacheConfig.CacheService cacheService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }

        if (StrUtil.isBlank(token)) {
            throw new BusinessException(401, "请先登录");
        }

        Object user = cacheService.get("token:" + token);
        if (user == null) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }

        request.setAttribute("currentUser", user);
        request.setAttribute("currentToken", token);
        return true;
    }
}

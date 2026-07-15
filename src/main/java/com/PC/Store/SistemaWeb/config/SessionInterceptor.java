package com.PC.Store.SistemaWeb.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);

        if (uri.startsWith("/admin")) {
            if (session == null || session.getAttribute("usuarioRol") == null || !"ADMIN".equals(session.getAttribute("usuarioRol"))) {
                response.sendRedirect("/");
                return false;
            }
        }

        return true;
    }
}

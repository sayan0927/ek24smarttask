package com.example.smart_task_management.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Http401And403Handler handles both authentication(HTTP 401) and access denied(HTTP 403) exceptions.
 * It implements both AuthenticationEntryPoint and AccessDeniedHandler interfaces to handle 401 (Unauthorized)
 * and 403 (Forbidden) HTTP status codes by redirecting the user to an access denied page.
 */
@Component
public class Http401And403Handler implements AuthenticationEntryPoint, AccessDeniedHandler {

    /**
     * Handles 401 (Unauthorized) error by redirecting the user to the access denied page.
     *
     * @param request   the HttpServletRequest
     * @param response  the HttpServletResponse
     * @throws IOException if an input or output error occurs while the servlet is handling the HTTP request
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException arg2) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.sendRedirect(request.getContextPath() + "/401");
    }

    /**
     * Handles 403 (Forbidden) error by redirecting the user to the access denied page.
     *
     * @param request               the HttpServletRequest
     * @param response              the HttpServletResponse
     * @param accessDeniedException the AccessDeniedException
     * @throws IOException      if an input or output error occurs while the servlet is handling the HTTP request
     * @throws ServletException if the request could not be handled
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.sendRedirect(request.getContextPath() + "/403");
    }
}
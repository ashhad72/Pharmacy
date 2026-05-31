package com.sda.pharmacy.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false); // Fetch session if it exists; don't create a new one

        // If no session exists OR the specific login attribute isn't there, bounce them out!
        if (session == null || session.getAttribute("loggedInUser") == null) {
            // Send them back to login with an unauthorized error message flag
            response.sendRedirect("/login?error=unauthorized");
            return false; // Halts the request processing pipeline immediately
        }

        return true; // Token found! Let them proceed to their controller method safely
    }
}
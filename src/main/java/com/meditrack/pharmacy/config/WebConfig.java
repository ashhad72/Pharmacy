package com.meditrack.pharmacy.config;

import com.meditrack.pharmacy.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**") // Apply security intercept to all URL routes globally
                .excludePathPatterns(
                        "/",                  // Allow public landing page
                        "/login",             // Allow login GET route
                        "/css/**",            // Allow loading styles
                        "/js/**",             // Allow autocomplete script engine
                        "/images/**",         // Allow static images assets
                        "/webjars/**"         // Allow bootstrap dependencies if used
                );
    }
}
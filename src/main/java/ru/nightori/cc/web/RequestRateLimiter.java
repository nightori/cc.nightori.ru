package ru.nightori.cc.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.nightori.cc.exceptions.IllegalHeaderException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class RequestRateLimiter implements WebMvcConfigurer {

    @Autowired
    ClientCacheService service;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // create and add an interceptor only for /api (create and delete methods)
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                // try to add the IP to the service's cache, if it succeeds - nothing will happen
                // if it fails - LimitExceededException will be thrown and handled elsewhere
                service.tryAccess(getIP(request));
                return true;
            }
        }).addPathPatterns("/api");
    }

    // helper function to get client's IP address
    private String getIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null) {
            String[] ips = ip.split(",");

            // this is a way to detect header tampering
            // if everything's legit, there should be only 2 IPs here
            if (ips.length != 2) {
                throw new IllegalHeaderException("Illegal ip header: "+ip);
            }

            // the first one belongs to the client
            return ips[0];
        }
        else {
            return request.getRemoteAddr();
        }
    }
}

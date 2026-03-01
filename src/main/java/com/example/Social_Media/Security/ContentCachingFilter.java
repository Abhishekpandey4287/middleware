package com.example.Social_Media.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Replaces the incoming request with a CachedBodyHttpServletRequest so that
 * the JSON body can be read multiple times — once by JwtAuthenticationFilter
 * to extract the "action" field, and again by the controller.
 *
 * MUST run before all other filters (HIGHEST_PRECEDENCE).
 *
 * NOTE: The previous version used ContentCachingRequestWrapper, which only
 * caches the body AFTER the stream is read for the first time — so
 * getContentAsByteArray() returned empty bytes in JwtAuthenticationFilter.
 * CachedBodyHttpServletRequest fixes this by reading eagerly in its constructor.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ContentCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // Wrap with our eager-caching wrapper instead of ContentCachingRequestWrapper
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        chain.doFilter(cachedRequest, response);
    }
}
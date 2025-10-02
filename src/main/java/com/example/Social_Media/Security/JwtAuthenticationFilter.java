//package com.example.Social_Media.Security;
//
//import com.example.Social_Media.Utility.JwtUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//
//            if (jwtUtil.validateToken(token)) {
//                String email = jwtUtil.extractEmail(token);
//                // You could set authentication in SecurityContext here if you want roles
//                request.setAttribute("userEmail", email); // 👈 custom attribute for controllers
//            } else {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Invalid or expired token");
//                return;
//            }
//        } else {
//            // For endpoints requiring authentication, token missing = reject
//            if (!request.getRequestURI().contains("/api/app/process")) { // allow login
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Missing Authorization header");
//                return;
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
//
